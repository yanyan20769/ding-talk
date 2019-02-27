package per.yan.ding.util.lock;

/**
 * @author gaoyan
 * @date 2019/2/26 19:55
 */
public class DistributeLockHolder {
    private String lock;

    public DistributeLockHolder() {
    }

    public String getLock() {
        return this.lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }
}
