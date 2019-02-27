package per.yan.ding.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xuyg
 * @create 2018-10-22 15:24
 **/

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SimpleLock {
    /**
     * 锁的key值
     */
    String lockKey();

    /**
     * 过期时间默认5S
     */
    int expiredSeconds() default 5;

    /**
     * 重试次数 默认1
     */
    int retryCount() default 1;

    /**
     * 重试间隔 默认1S
     */
    int retryDelaySeconds() default 1;
}
