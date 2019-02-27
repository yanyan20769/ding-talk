package per.yan.ding.model.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import per.yan.ding.model.constant.DDMsgTypeEnum;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author gaoyan
 * @date 2018/11/5 20:27
 */
@Data
@NoArgsConstructor
@ApiModel("text 消息 模型")
public class DDTextMsgDTO extends DDBaseMsgDTO {

    @ApiModelProperty("消息内容")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    @ApiModelProperty("是否@所有人")
    private Boolean atAll;

    @ApiModelProperty("需要@的人的手机号")
    private List<String> atMobiles;

    @Builder
    public DDTextMsgDTO(Integer msgLevel, Set<String> tokens, Boolean sendAll, Date createdTime, String env, String appName, String content, Boolean atAll, List<String> atMobiles) {
        super(msgLevel, tokens, sendAll, createdTime, env, appName);
        this.content = content;
        this.atAll = atAll;
        this.atMobiles = atMobiles;
    }

    @Override
    public String toJsonString() {
        JSONObject result = new JSONObject();
        JSONObject content = new JSONObject();
        JSONObject at = new JSONObject();
        if (this.getAtAll() != null && this.getAtAll()) {
            at.put("isAtAll", true);
        } else {
            if (!CollectionUtils.isEmpty(this.getAtMobiles())) {
                at.put("atMobiles", this.getAtMobiles());
            }
        }
        content.put("content", appendPrefix() + this.getContent());
        result.put("msgtype", DDMsgTypeEnum.TEXT.getMessageType());
        result.put("text", content);
        result.put("at", at);
        return result.toJSONString();
    }
}
