package per.yan.ding.model.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gaoyan
 * @date 2019/2/20 13:57
 */
@ApiModel("消息级别")

@Getter
@AllArgsConstructor
public enum DDMsgLevelEnum {
    /**
     * 消息级别
     */
    @ApiModelProperty("高")
    HIGHER(1),
    @ApiModelProperty("常规")
    NORMAL(2),
    @ApiModelProperty("低")
    LOWER(3);

    private int code;
}
