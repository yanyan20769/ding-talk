package per.yan.ding.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis限流注解
 * @author gaoyan
 * @date 2019/2/21 10:05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiter {

    /**
     * 限流key
     */
    String key();

    /**
     * expire时间内允许通过的请求数
     */
    long limit() default 20;

    /**
     * 过期时间，单位秒
     */
    long expire() default 60;
}
