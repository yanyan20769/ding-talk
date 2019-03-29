package per.yan.ding.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import per.yan.ding.model.vo.DataResponse;
import per.yan.ding.util.el.AspectSupportUtils;

import java.lang.reflect.Method;

/**
 * @author gaoyan
 * @date 2019/1/15 10:05
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    public static final String USER_SYSTEM = "System";

    public static final int SUCCESS_CODE = 200;

    @Pointcut("@annotation(per.yan.ding.aspect.Log)")
    public void doPerform() {

    }

    @AfterReturning(pointcut = "doPerform()", returning = "result")
    public void determine(JoinPoint jp, Object result) {
        if (result instanceof DataResponse) {
            DataResponse response = (DataResponse) result;
            if (response.getResultCode() == SUCCESS_CODE) {
                MethodSignature signature = (MethodSignature) jp.getSignature();
                if (null != signature) {
                    Method method = signature.getMethod();
                    if (null != method && method.isAnnotationPresent(Log.class)) {
                        Log log = method.getAnnotation(Log.class);
                        String orderNoKey = log.orderNoKey();
                        String createdByKey = log.createdByKey();
                        //使用spring EL表达式获取参数中的值
                        String orderNo = (String) AspectSupportUtils.getKeyValue(jp, orderNoKey);
                        Integer createdBy;
                        if (USER_SYSTEM.equals(createdByKey)) {
                            createdBy = 0;
                        } else {
                            createdBy = (Integer) AspectSupportUtils.getKeyValue(jp, createdByKey);
                        }
                        /// TODO: 2019/3/29 调用记录日志的service记录日志

                    }
                }
            }
        }
    }
}
