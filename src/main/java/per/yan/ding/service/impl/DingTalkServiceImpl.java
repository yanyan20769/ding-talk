package per.yan.ding.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.yan.ding.handler.DingTalkSendHandler;
import per.yan.ding.model.DDItemMessageDO;
import per.yan.ding.model.DDMessageDO;
import per.yan.ding.model.DDResponse;
import per.yan.ding.model.constant.DDMessageResultEnum;
import per.yan.ding.model.dto.DDBaseMsgDTO;
import per.yan.ding.model.dto.DDMarkdownMsgDTO;
import per.yan.ding.model.dto.DDTextMsgDTO;
import per.yan.ding.model.vo.DDMessageVO;
import per.yan.ding.model.vo.DataResponse;
import per.yan.ding.service.DingTalkService;
import per.yan.ding.util.NumProducerUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gaoyan
 * @date 2018/11/5 20:41
 */
@Slf4j
@Service
public class DingTalkServiceImpl implements DingTalkService {

    @Autowired
    private DingTalkSendHandler handler;

    @Override
    public DataResponse sendTextImmediately(DDTextMsgDTO ddTextMsgDTO) {
        return sendMessageImmediately(ddTextMsgDTO);
    }

    @Override
    public DataResponse sendMarkdownImmediately(DDMarkdownMsgDTO ddMarkdownMsgDTO) {
        return sendMessageImmediately(ddMarkdownMsgDTO);
    }

    @Override
    public DataResponse<String> sendText(DDTextMsgDTO ddTextMsgDTO) {
        return saveAllInformation(ddTextMsgDTO);
    }

    @Override
    public DataResponse<String> sendMarkdown(DDMarkdownMsgDTO ddMarkdownMsgDTO) {
        return saveAllInformation(ddMarkdownMsgDTO);
    }

    @Override
    public DataResponse<DDMessageVO> searchMessageInfo(String messageNo) {
        return new DataResponse<>(handler.searchMessageInfo(messageNo));
    }

    @Override
    public DataResponse executeMessageSendTask() {
        Set<String> validTokens = getValidTokens();
        while (CollectionUtils.isNotEmpty(validTokens)) {
            process(validTokens);
            validTokens = getValidTokens();
        }
        return new DataResponse();
    }

    private void process(Set<String> tokens) {
        if (CollectionUtils.isNotEmpty(tokens)) {
            //所有的messageNo
            Set<String> validMessageNoSet = new HashSet<>();
            //key-token，value-messageNo
            Map<String, String> validTokenMap = new HashMap<>(32);
            tokens.stream()
                    //限流过滤
                    .filter(handler::isNotLimit)
                    .forEach(t -> {
                                String messageNo = handler.popWaitForSend(t);
                                if (StringUtils.isNotEmpty(messageNo)) {
                                    validMessageNoSet.add(messageNo);
                                    validTokenMap.put(t, messageNo);
                                }
                            }
                    );
            //将所有的message内容一次性查出，再根据token所需要的message内容分发到每个token对应的异步线程
            Map<String, DDMessageDO> messageInfoMap = prepareMap(validMessageNoSet);
            if (MapUtils.isNotEmpty(validTokenMap) && MapUtils.isNotEmpty(messageInfoMap)) {
                Map<String, DDMessageDO> finalMessageInfoMap = messageInfoMap;
                validTokenMap.keySet()
                        .stream()
                        .filter(k -> null != k && !StringUtils.isEmpty(validTokenMap.get(k)))
                        .forEach(k -> {
                                    //每个token对应的待发message内容
                                    DDMessageDO messageDO = finalMessageInfoMap.get(validTokenMap.get(k));
                                    //每个token使用一个异步线程发送
                                    if (null != messageDO) {
                                        handler.sendMessageAsync(k, messageDO);
                                    }
                                }
                        );
            }
        }
    }

    private <T extends DDBaseMsgDTO> DataResponse sendMessageImmediately(T t) {
        boolean sendAll = t.getSendAll() != null && t.getSendAll();

        if (CollectionUtils.isNotEmpty(t.getTokens())) {
            Set<DDMessageResultEnum> itemResults = new HashSet<>();
            for (String token : t.getTokens()) {
                if (handler.isNotLimit(token)) {
                    DDResponse res = handler.send(token, t.toJsonString());
                    itemResults.add(assembleResult(res));
                } else {
                    if (sendAll) {
                        return getResultNow(DDMessageResultEnum.RATE_LIMIT);
                    } else {
                        itemResults.add(DDMessageResultEnum.RATE_LIMIT);
                    }
                }
            }
            return handleResult(itemResults, sendAll);
        }
        return getResultNow(DDMessageResultEnum.FAIL);
    }

    private <T extends DDBaseMsgDTO> DataResponse<String> saveAllInformation(T t) {
        String messageNo = NumProducerUtil.generateNumWithPrefix("M");
        //保存token，用于定时任务扫描 set结构
        //set名-固定字符 元素-token
        handler.saveTokens(t.getTokens());

        //保存消息内容 hash结构
        //hash名-固定字符 key-messageNo value-DDMessageDO
        handler.saveMessage(assembleMessageDO(messageNo, t));

        //待发列表 list结构
        //list名-token  元素-messageNo
        t.getTokens().forEach(token -> handler.saveWaitForSend(token, messageNo));

        //保存messageNo与token对应的发送状态 hash结构
        //hash名-messageNo key-token value-DDItemMessageDO
        handler.saveAllItems(t.getTokens().stream().map(token -> assembleItemMessageDO(messageNo, token)).collect(Collectors.toList()));

        //触发一次当前消息所有token的发送
        process(t.getTokens());

        return new DataResponse<>(messageNo);
    }

    private Map<String, DDMessageDO> prepareMap(Set<String> validMessageNoSet) {
        List<DDMessageDO> messageDOList = handler.batchQueryMessageDO(validMessageNoSet);
        //key-messageNo v-message内容
        Map<String, DDMessageDO> messageInfoMap = null;
        if (CollectionUtils.isNotEmpty(messageDOList)) {
            messageInfoMap = messageDOList.stream().collect(Collectors.toMap(DDMessageDO::getMessageNo, v -> v, (first, second) -> first));
        }
        return messageInfoMap;
    }

    private Set<String> getValidTokens() {
        Set<String> tokens = handler.queryAllTokens();
        return tokens.stream().filter(handler::isNotEmpty).collect(Collectors.toSet());
    }

    private <T extends DDBaseMsgDTO> DDMessageDO assembleMessageDO(String messageNo, T t) {
        return DDMessageDO
                .builder()
                .messageNo(messageNo)
                .msgLevel(t.getMsgLevel())
                .sendAll(t.getSendAll())
                .content(t.toJsonString())
                .build();
    }

    private DDItemMessageDO assembleItemMessageDO(String messageNo, String token) {
        return DDItemMessageDO
                .builder()
                .messageNo(messageNo)
                .token(token)
                .build();
    }

    private DDMessageResultEnum assembleResult(DDResponse response) {
        if (response != null) {
            if (response.getErrcode() == DDMessageResultEnum.SUCCESS.getCode()) {
                return DDMessageResultEnum.SUCCESS;
            } else if (response.getErrcode() == DDMessageResultEnum.RATE_LIMIT.getCode()) {
                return DDMessageResultEnum.RATE_LIMIT;
            }
        }
        return DDMessageResultEnum.FAIL;
    }

    private DataResponse<DDMessageResultEnum> handleResult(Set<DDMessageResultEnum> itemResults, boolean sendAll) {
        if (CollectionUtils.isNotEmpty(itemResults)) {
            if (sendAll) {
                if (itemResults.stream().allMatch(r -> DDMessageResultEnum.SUCCESS == r)) {
                    return getResultNow(DDMessageResultEnum.SUCCESS);
                } else if (itemResults.stream().allMatch(r -> DDMessageResultEnum.RATE_LIMIT == r)) {
                    return getResultNow(DDMessageResultEnum.RATE_LIMIT);
                } else {
                    return getResultNow(DDMessageResultEnum.FAIL);
                }
            } else {
                if (itemResults.stream().anyMatch(r -> DDMessageResultEnum.SUCCESS == r)) {
                    return getResultNow(DDMessageResultEnum.SUCCESS);
                } else if (itemResults.stream().anyMatch(r -> DDMessageResultEnum.FAIL == r)) {
                    return getResultNow(DDMessageResultEnum.FAIL);
                } else {
                    return getResultNow(DDMessageResultEnum.RATE_LIMIT);
                }
            }
        }
        return getResultNow(DDMessageResultEnum.FAIL);
    }

    private DataResponse<DDMessageResultEnum> getResultNow(DDMessageResultEnum resultEnum) {
        return new DataResponse<>(resultEnum);
    }
}
