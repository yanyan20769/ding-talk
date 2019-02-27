package per.yan.ding.model.vo;

/**
 * @author gaoyan
 * @date 2019/2/26 19:36
 */

public class DataResponse<T> {

    private int resultCode;

    private T data;

    public DataResponse(){
        this(null);
    }

    public DataResponse(T data) {
        this.resultCode = 200;
        this.data = data;
    }
}
