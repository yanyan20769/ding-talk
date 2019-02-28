package per.yan.ding.model.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import per.yan.ding.model.constant.DDMarkdownMsgTextUtil;
import per.yan.ding.model.constant.DDMsgTypeEnum;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gaoyan
 * @date 2018/11/5 22:10
 */
@ApiModel("钉钉markdown消息")
@Data
@NoArgsConstructor
public class DDMarkdownMsgDTO extends DDBaseMsgDTO {

    @ApiModelProperty("首屏会话透出的展示内容")
    @NotBlank(message = "title不能为空")
    private String title;

    @ApiModelProperty("消息内容 格式可以通过DDMarkdownMsgTextUtil类设置")
    @NotBlank(message = "内容不能为空")
    private String text;

    @ApiModelProperty("是否@所有人")
    private Boolean atAll;

    @ApiModelProperty("需要@的人的手机号")
    private List<String> atMobiles;

    @Builder
    public DDMarkdownMsgDTO(Integer msgLevel, Set<String> tokens, Boolean sendAll, Date createdTime, String env, String appName, String title, String text, Boolean atAll, List<String> atMobiles) {
        super(msgLevel, tokens, sendAll, createdTime, env, appName);
        this.title = title;
        this.text = text;
        this.atAll = atAll;
        this.atMobiles = atMobiles;
    }


    @Override
    public String toJsonString() {
        JSONObject result = new JSONObject();
        result.put("msgtype", DDMsgTypeEnum.MARKDOWN.getMessageType());
        Map<String, Object> markdown = new HashMap<>(16);
        markdown.put("title", this.getTitle());
        StringBuilder text = new StringBuilder();
        String envStr = appendPrefix();
        if (!StringUtils.isEmpty(envStr)) {
            text.append(DDMarkdownMsgTextUtil.getHeaderText(4, envStr));
            text.append("\n");
        }
        markdown.put("text", text.toString() + this.getText());
        result.put("markdown", markdown);
        JSONObject at = new JSONObject();
        if (this.getAtAll() != null && this.getAtAll()) {
            at.put("isAtAll", true);
        } else {
            if (!CollectionUtils.isEmpty(this.getAtMobiles())) {
                at.put("atMobiles", this.getAtMobiles());
            }
        }
        result.put("at", at);
        return result.toJSONString();
    }
}
