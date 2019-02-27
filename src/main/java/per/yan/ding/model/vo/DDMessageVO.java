package per.yan.ding.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaoyan
 * @date 2019/2/25 15:34
 */
@ApiModel("钉钉消息对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DDMessageVO {

    private String messageNo;

    private Integer msgLevel;

    private Boolean sendAll;

    private String content;

    private Result result;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private Integer resultCode;
        private String resultMsg;
    }

}
