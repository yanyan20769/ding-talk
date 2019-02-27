package per.yan.ding.util.lock;

/**
 * @author gaoyan
 * @date 2019/2/26 19:57
 */
public interface DistributeLockHelper {
    DistributeLockHolder getLock(String var1);

    void release(DistributeLockHolder var1);

    Boolean isLocked(String var1);
}
