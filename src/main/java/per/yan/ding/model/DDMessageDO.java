package per.yan.ding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 钉钉消息持久化对象 redis
 *
 * @author gaoyan
 * @date 2019/2/21 16:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DDMessageDO implements Serializable {
    private String messageNo;

    private Integer msgLevel;

    private Boolean sendAll;

    private String content;

    private Result result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result implements Serializable {
        /**
         * 发送结果
         */
        private Integer resultCode;
        /**
         * 结果描述
         */
        private String resultMsg;
    }
}
