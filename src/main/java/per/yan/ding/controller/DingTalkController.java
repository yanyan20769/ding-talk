package per.yan.ding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import per.yan.ding.api.IDingTalkApi;
import per.yan.ding.model.dto.DDMarkdownMsgDTO;
import per.yan.ding.model.dto.DDTextMsgDTO;
import per.yan.ding.model.vo.DDMessageVO;
import per.yan.ding.model.vo.DataResponse;
import per.yan.ding.service.DingTalkService;

import javax.validation.constraints.NotBlank;

/**
 * @author gaoyan
 * @date 2018/11/5 20:26
 */
@RestController
public class DingTalkController implements IDingTalkApi {

    @Autowired
    private DingTalkService service;

    @Override
    public DataResponse sendTextImmediately(@RequestBody @Validated DDTextMsgDTO ddTextMsgDTO) {
        return service.sendTextImmediately(ddTextMsgDTO);
    }

    @Override
    public DataResponse sendMarkdownImmediately(@RequestBody @Validated DDMarkdownMsgDTO ddMarkdownMsgDTO) {
        return service.sendMarkdownImmediately(ddMarkdownMsgDTO);
    }

    @Override
    public DataResponse sendText(@RequestBody @Validated DDTextMsgDTO ddTextMsgDTO) {
        return service.sendText(ddTextMsgDTO);
    }

    @Override
    public DataResponse sendMarkdown(@RequestBody @Validated DDMarkdownMsgDTO ddMarkdownMsgDTO) {
        return service.sendMarkdown(ddMarkdownMsgDTO);
    }

    @Override
    public DataResponse<DDMessageVO> searchMessageInfo(@PathVariable("messageNo") @NotBlank String messageNo) {
        return service.searchMessageInfo(messageNo);
    }

    @Override
    public DataResponse executeMessageSendTask() {
        return service.executeMessageSendTask();
    }
}
