package per.yan.ding.exception;

/**
 * @author gaoyan
 * @date 2019/2/26 19:53
 */
public class LockException extends RuntimeException {
    public LockException(String msg){
        super(msg);
    }
}
