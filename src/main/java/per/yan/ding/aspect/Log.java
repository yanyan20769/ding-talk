package per.yan.ding.aspect;

import java.lang.annotation.*;

/**
 * 日志记录  注解
 * @author gaoyan
 * @date 2019/1/15 10:02
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {

    String orderNoKey();

    String createdByKey() default "System";
}
