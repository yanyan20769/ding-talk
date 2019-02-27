package per.yan.ding.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * @author gaoyan
 * @date 2018/11/5 22:01
 */
@ApiModel("钉钉消息base DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DDBaseMsgDTO {

    @ApiModelProperty("消息优先级 1-高；2-常规；3-低 见DDMsgLevelEnum")
    @NotNull(message = "消息优先级不能为空")
    private Integer msgLevel;
    @ApiModelProperty("机器人token列表")
    @NotEmpty(message = "机器人列表不能为空")
    private Set<String> tokens;
    @ApiModelProperty("是否需要所有机器人发送该消息 空等同false")
    private Boolean sendAll;
    @ApiModelProperty("消息产生时间")
    private Date createdTime;
    @ApiModelProperty("消息所属环境")
    private String env;
    @ApiModelProperty("消息所属工程")
    private String appName;

    public abstract String toJsonString();

    /**
     * 优先级为 HIGHER的消息需要在消息内容前加上 时间、环境、工程等信息
     */
    protected String appendPrefix() {
        StringBuilder sb = new StringBuilder();
        if (this.getCreatedTime() != null) {
            String dateStr = new SimpleDateFormat("MM-dd HH:mm:ss").format(this.getCreatedTime());
            sb.append(dateStr);
            sb.append(" ");
        }
        if (!StringUtils.isEmpty(this.getEnv())) {
            sb.append(this.getEnv());
            sb.append(" ");
        }
        if (!StringUtils.isEmpty(this.getAppName())) {
            sb.append(this.getAppName());
            sb.append(" ");
        }
        if (sb.length() > 0) {
            sb = new StringBuilder(sb.substring(0, sb.length() - 1));
            sb.append("\n");
        }
        return sb.toString();
    }
}
