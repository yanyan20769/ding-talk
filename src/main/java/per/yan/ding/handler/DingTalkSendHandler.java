package per.yan.ding.handler;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import per.yan.ding.aspect.RateLimiter;
import per.yan.ding.client.DingTalkClient;
import per.yan.ding.model.DDItemMessageDO;
import per.yan.ding.model.DDMessageDO;
import per.yan.ding.model.DDResponse;
import per.yan.ding.model.constant.CacheNameSpace;
import per.yan.ding.model.constant.DDMessageResultEnum;
import per.yan.ding.model.vo.DDMessageVO;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gaoyan
 * @date 2019/2/21 10:38
 */
@Slf4j
@Component
public class DingTalkSendHandler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DingTalkClient client;

    @Async("taskExecutor")
    public void sendMessageAsync(String token, DDMessageDO m) {
        DDResponse response = send(token, m.getContent());
        handleAfterSent(response, token, m);
    }

    @RateLimiter(key = "#token")
    public boolean isNotLimit(String token) {
        return true;
    }

    public DDMessageVO searchMessageInfo(String messageNo) {
        DDMessageVO vo = new DDMessageVO();
        DDMessageDO message = queryMessageDO(messageNo);
        BeanUtils.copyProperties(message, vo);
        return vo;
    }

    public DDResponse send(String token, String content) {
        JSONObject stringObj = JSON.parseObject(content);
        DDResponse response;
        try {
            response = client.send(token, stringObj);
        } catch (Exception e) {
            log.error("发送钉钉消息失败！e:{}", e);
            return new DDResponse(DDMessageResultEnum.RATE_LIMIT.getCode(), DDMessageResultEnum.RATE_LIMIT.getValue());
        }
        return response;
    }

    private void handleAfterSent(DDResponse response, String token, DDMessageDO m) {
        boolean sendAll = null != m.getSendAll() && m.getSendAll();
        //非限流失败 包含发送成功和钉钉返回失败两种情况
        if (isNotRateLimit(response)) {
            //更新每条messageNo-token对应的发送状态
            updateItemStatus(assembleItemMessageDO(m.getMessageNo(), token, response));
            appendResult(m, response);
            if (response.getErrcode() == DDMessageResultEnum.SUCCESS.getCode()
                    && sendAll && !hasSentAll(m.getMessageNo())) {
                //子消息发送成功时，发送所有机器人但还有机器人未发送的消息本次不更改主消息状态
                return;
            }
            //其他都需要更改主消息的状态
            updateMessageStatus(m);
        } else {
            if (isRateLimit(response) && !sendAll && hasSentAny(m.getMessageNo())) {
                //被限流的且不是发送所有机器人的消息若有其它机器人已经发送，则不需要再放回队列头部
                return;
            }
            //发送失败需要重新放回待发队列中
            saveWaitForSend(token, m.getMessageNo());
        }
    }

    private DDItemMessageDO assembleItemMessageDO(String messageNo, String token, DDResponse response) {
        DDItemMessageDO item = new DDItemMessageDO();
        item.setMessageNo(messageNo);
        item.setToken(token);
        if (response.getErrcode() != null) {
            item.setStatus(response.getErrcode());
            if (response.getErrcode() != DDMessageResultEnum.SUCCESS.getCode() && response.getErrcode() != DDMessageResultEnum.RATE_LIMIT.getCode()) {
                item.setStatus(DDMessageResultEnum.FAIL.getCode());
            }
        }
        return item;
    }

    private void appendResult(DDMessageDO m, DDResponse response) {
        m.setResultCode(response.getErrcode());
        m.setResultMsg(response.getErrmsg());
    }

    private boolean hasSentAll(String messageNo) {
        List<DDItemMessageDO> itemList = queryItemMessageDO(messageNo);
        return CollectionUtils.isNotEmpty(itemList)
                && itemList.stream().allMatch(i -> null != i.getStatus() && i.getStatus() == DDMessageResultEnum.SUCCESS.getCode());
    }

    private boolean hasSentAny(String messageNo) {
        List<DDItemMessageDO> itemList = queryItemMessageDO(messageNo);
        return CollectionUtils.isNotEmpty(itemList)
                && itemList.stream().anyMatch(i -> null != i.getStatus() && i.getStatus() == DDMessageResultEnum.SUCCESS.getCode());
    }

    private boolean isRateLimit(DDResponse response) {
        return response.getErrcode() != null && response.getErrcode() == DDMessageResultEnum.RATE_LIMIT.getCode();
    }

    private boolean isNotRateLimit(DDResponse response) {
        return response.getErrcode() != null && response.getErrcode() != DDMessageResultEnum.RATE_LIMIT.getCode();
    }

    private void updateMessageStatus(DDMessageDO messageDO) {
        String hashName = getMsgContentHash();
        redisTemplate.opsForHash().put(hashName, messageDO.getMessageNo(), messageDO);
    }

    public void saveTokens(Set<String> tokens) {
        String setName = getAllTokensSet();
        redisTemplate.opsForSet().add(setName, tokens.toArray(new String[]{}));
    }

    public void saveMessage(DDMessageDO messageDO) {
        String hashName = getMsgContentHash();
        redisTemplate.opsForHash().put(hashName, messageDO.getMessageNo(), messageDO);
    }

    public void saveWaitForSend(String token, String messageNo) {
        String listName = getWaitForSendList(token);
        redisTemplate.opsForList().rightPush(listName, messageNo);
    }

    public void saveAllItems(List<DDItemMessageDO> itemList) {
        if (CollectionUtils.isNotEmpty(itemList)) {
            Map<String, DDItemMessageDO> itemMap = itemList.stream()
                    .collect(Collectors.toMap(DDItemMessageDO::getToken, v -> v, (first, second) -> first));
            String hashName = getItemContentHash(itemList.get(0).getMessageNo());
            redisTemplate.opsForHash().putAll(hashName, itemMap);
        }
    }

    public boolean isNotEmpty(String token) {
        String listName = getWaitForSendList(token);
        Long size = redisTemplate.opsForList().size(listName);
        return size != null && size > 0L;
    }

    public Set<String> queryAllTokens() {
        String setName = getAllTokensSet();
        return (Set<String>) redisTemplate.opsForSet().members(setName);
    }

    public String popWaitForSend(String token) {
        String listName = getWaitForSendList(token);
        return (String) redisTemplate.opsForList().rightPop(listName);
    }

    private void updateItemStatus(DDItemMessageDO item) {
        String hashName = getItemContentHash(item.getMessageNo());
        redisTemplate.opsForHash().put(hashName, item.getToken(), item);
    }

    private List<DDItemMessageDO> queryItemMessageDO(String messageNo) {
        String hashName = getItemContentHash(messageNo);
        Set<String> tokens = (Set<String>) redisTemplate.opsForHash().keys(hashName);
        List<DDItemMessageDO> paramsList = redisTemplate.opsForHash().multiGet(hashName, tokens);
        return paramsList;
    }

    private DDMessageDO queryMessageDO(String messageNo) {
        String hashName = getMsgContentHash();
        return (DDMessageDO) redisTemplate.opsForHash().get(hashName, messageNo);
    }

    public List<DDMessageDO> batchQueryMessageDO(Set<String> messageNos) {
        String hashName = getMsgContentHash();
        return (List<DDMessageDO>) redisTemplate.opsForHash().multiGet(hashName, messageNos);
    }

    private String getMsgContentHash() {
        return CacheNameSpace.Notification.DING_HASH_MESSAGE_CONTENT;
    }

    private String getAllTokensSet() {
        return CacheNameSpace.Notification.DING_SET_ALL_TOKENS;
    }

    private String getWaitForSendList(String token) {
        return MessageFormat.format(CacheNameSpace.Notification.DING_LIST_WAIT_FOR_SEND, token);
    }

    private String getItemContentHash(String messageNo) {
        return MessageFormat.format(CacheNameSpace.Notification.DING_HASH_MESSAGE_TOKEN, messageNo);
    }
}
