package per.yan.ding.service;

import per.yan.ding.model.dto.DDMarkdownMsgDTO;
import per.yan.ding.model.dto.DDTextMsgDTO;
import per.yan.ding.model.vo.DDMessageVO;
import per.yan.ding.model.vo.DataResponse;

/**
 * @author gaoyan
 * @date 2018/11/5 20:41
 */
public interface DingTalkService {

    DataResponse sendTextImmediately(DDTextMsgDTO ddTextMsgDTO);

    DataResponse sendMarkdownImmediately(DDMarkdownMsgDTO ddMarkdownMsgDTO);

    DataResponse<String> sendText(DDTextMsgDTO ddTextMsgDTO);

    DataResponse<String> sendMarkdown(DDMarkdownMsgDTO ddMarkdownMsgDTO);

    DataResponse<DDMessageVO> searchMessageInfo(String messageNo);

    DataResponse executeMessageSendTask();
}
