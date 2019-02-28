package per.yan.ding.model.vo;

import lombok.Data;

/**
 * @author gaoyan
 * @date 2019/2/26 19:36
 */

@Data
public class DataResponse<T> {

    private int resultCode;

    private T data;

    public DataResponse() {
        this(200);
    }

    public DataResponse(int resultCode) {
        this(resultCode, null);
    }

    public DataResponse(T data) {
        this(200, data);
    }

    public DataResponse(int resultCode, T data) {
        this.resultCode = resultCode;
        this.data = data;
    }
}
