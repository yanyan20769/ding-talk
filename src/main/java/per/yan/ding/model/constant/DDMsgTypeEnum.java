package per.yan.ding.model.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gaoyan
 * @date 2018/11/5 21:37
 */

@Getter
@AllArgsConstructor
@ApiModel("消息类型")
public enum DDMsgTypeEnum {
    /**
     * 钉钉消息类型
     */
    @ApiModelProperty("文本")
    TEXT("text"),
    @ApiModelProperty("link")
    LINK("link"),
    @ApiModelProperty("markdown")
    MARKDOWN("markdown"),
    @ApiModelProperty("整体跳转")
    ACTION_CARD("actionCard"),
    @ApiModelProperty("独立跳转")
    FEED_CARD("feedCard");

    private String messageType;
}
