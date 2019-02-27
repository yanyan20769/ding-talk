package per.yan.ding.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gaoyan
 * @date 2019/2/26 09:30
 */
@Getter
@AllArgsConstructor
public enum DDMessageResultEnum {

    /**
     *
     */
    SUCCESS(0, "发送成功"),
    RATE_LIMIT(1, "钉钉限流"),
    FAIL(2, "发送失败");

    private int code;
    private String value;
}
