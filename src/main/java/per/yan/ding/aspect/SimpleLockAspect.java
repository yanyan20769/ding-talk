package per.yan.ding.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import per.yan.ding.exception.LockException;
import per.yan.ding.model.constant.CacheNameSpace;
import per.yan.ding.util.el.AspectSupportUtils;
import per.yan.ding.util.lock.DistributeLockHolder;
import per.yan.ding.util.lock.RedisDistributeLockHelper;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * @author xuyg
 * @create 2018-10-22 15:28
 **/
@Aspect
@Component
@Slf4j
public class SimpleLockAspect {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(per.yan.ding.aspect.SimpleLock)")
    public void doPerform1() {
    }

    @Around("doPerform1()")
    public Object determine(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method targetMethod = signature.getMethod();
        if (targetMethod.isAnnotationPresent(SimpleLock.class)) {
            SimpleLock simpleLock = targetMethod.getAnnotation(SimpleLock.class);
            String lockKey = simpleLock.lockKey();
            lockKey = (String) AspectSupportUtils.getKeyValue(jp, lockKey);
            lockKey = appendKeyPrefix(lockKey);
            Integer expireSeconds = simpleLock.expiredSeconds() < 1 ? 5 : simpleLock.expiredSeconds();
            int retryCount = simpleLock.retryCount() < 0 ? 1 : simpleLock.retryCount();
            int retryDelay = simpleLock.retryDelaySeconds() < 0 ? 1 : simpleLock.retryDelaySeconds();
            RedisDistributeLockHelper lockHelper = new RedisDistributeLockHelper("ovc-purchase", stringRedisTemplate);
            lockHelper.setMaxLockTimeSeconds(expireSeconds);
            if (retryCount == 0) {
                return lockExecute(jp, lockKey, lockHelper);
            }
            try {
                return lockExecute(jp, lockKey, lockHelper);
            } catch (LockException e) {
                while (retryCount-- > 0) {
                    Thread.currentThread().join(retryDelay * 1000);
                    try {
                        return lockExecute(jp, lockKey, lockHelper);
                    } catch (LockException ignored) {
                    }
                }
                throw new LockException("get lock: " + lockKey + " error , please try again later");
            }
        } else {
            return jp.proceed();
        }
    }

    private Object lockExecute(ProceedingJoinPoint jp, String lockKey, RedisDistributeLockHelper lockHelper) throws Throwable {
        DistributeLockHolder lockHolder = null;
        try {
            lockHolder = lockHelper.getLock(lockKey);
            if (lockHolder != null) {
                return jp.proceed();
            } else {
                throw new LockException("get lock: " + lockKey + " error , please try again later");
            }
        } finally {
            if (lockHolder != null) {
                lockHelper.release(lockHolder);
            }
        }
    }

    private String appendKeyPrefix(String lockKey) {
        return MessageFormat.format(CacheNameSpace.Notification.SIMPLE_LOCK, lockKey);
    }

}
