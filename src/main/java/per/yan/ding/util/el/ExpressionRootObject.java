package per.yan.ding.util.el;

/**
 * @author gaoyan
 * @date 2019/1/14 17:42
 */
public class ExpressionRootObject {
    private final Object object;

    private final Object[] args;

    public ExpressionRootObject(Object object, Object[] args) {
        this.object = object;
        this.args = args;
    }

    public Object getobject() {
        return object;
    }

    public Object[] getArgs() {
        return args;
    }
}