package per.yan.ding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import per.yan.ding.model.DDResponse;

/**
 * @author gaoyan
 * @date 2018/11/5 20:58
 */
@FeignClient(name = "dingTalkClient", url = "#{'${ding.robot.address}'}")
@RequestMapping("/robot")
public interface DingTalkClient {

    /**
     * 发送钉钉消息
     */
    @PostMapping("/send")
    DDResponse send(@RequestParam("access_token") String accessToken, @RequestBody Object content);
}
