package per.yan.ding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 钉钉子消息 redis持久化对象
 *
 * @author gaoyan
 * @date 2019/2/21 16:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DDItemMessageDO implements Serializable {

    private String messageNo;

    private String token;

    /**
     * 发送状态
     */
    private Integer status;
}
