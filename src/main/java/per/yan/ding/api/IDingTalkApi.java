package per.yan.ding.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import per.yan.ding.model.dto.DDMarkdownMsgDTO;
import per.yan.ding.model.dto.DDTextMsgDTO;
import per.yan.ding.model.vo.DDMessageVO;
import per.yan.ding.model.vo.DataResponse;

/**
 * @author gaoyan
 * @date 2018/11/5 20:24
 */
@Api(description = "钉钉通知")
@RequestMapping("/dingtalk")
public interface IDingTalkApi {

    @ApiOperation("text消息 即时获取发送结果 消息不会保存")
    @PostMapping("/send-text-sync")
    DataResponse sendTextImmediately(@RequestBody DDTextMsgDTO ddTextMsgDTO);

    @ApiOperation("markdown消息 即时获取发送结果 消息不会保存")
    @PostMapping("/send-markdown-sync")
    DataResponse sendMarkdownImmediately(@RequestBody DDMarkdownMsgDTO ddMarkdownMsgDTO);

    @ApiOperation("text消息 异步发送消息 消息会保存 消息结果以及相关内容需要使用searchMessageInfo()接口查询")
    @PostMapping("/send-text")
    DataResponse<String> sendText(@RequestBody DDTextMsgDTO ddTextMsgDTO);

    @ApiOperation("markdown消息 异步发送消息 消息会保存 消息结果以及相关内容需要使用searchMessageInfo()接口查询")
    @PostMapping("/send-markdown")
    DataResponse<String> sendMarkdown(@RequestBody DDMarkdownMsgDTO ddMarkdownMsgDTO);

    @ApiOperation("查询消息是否发送")
    @GetMapping("/search/{messageNo}")
    DataResponse<DDMessageVO> searchMessageInfo(@PathVariable("messageNo") String messageNo);

    @ApiOperation("定时任务调用接口 参数为每次调用 每个token下取几条消息")
    @PostMapping("/task/send")
    DataResponse executeMessageSendTask();
}
