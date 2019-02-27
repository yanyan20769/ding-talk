package per.yan.ding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gaoyan
 * @date 2019/2/20 09:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DDResponse {
    private Integer errcode;
    private String errmsg;
}
