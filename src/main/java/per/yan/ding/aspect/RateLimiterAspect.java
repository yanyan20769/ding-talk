package per.yan.ding.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import per.yan.ding.model.constant.CacheNameSpace;
import per.yan.ding.util.el.AspectSupportUtils;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaoyan
 * @date 2019/2/21 10:10
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    private DefaultRedisScript<Long> getRedisScript;

    @PostConstruct
    public void init() {
        getRedisScript = new DefaultRedisScript<>();
        getRedisScript.setResultType(Long.class);
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/rateLimiter.lua")));
    }

    @Pointcut("@annotation(per.yan.ding.aspect.RateLimiter)")
    public void rateLimiter() {

    }

    @Around("@annotation(rateLimiter)")
    public boolean execute(ProceedingJoinPoint jp, RateLimiter rateLimiter) {
        Signature signature = jp.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("Annotation @RateLimiter must used on method!");
        }
        String tokenKey = rateLimiter.key();
        String token = (String) AspectSupportUtils.getKeyValue(jp, tokenKey);
        long limitTimes = rateLimiter.limit();
        long expireTime = rateLimiter.expire();

        String limitKey = getRateLimiterKey(token);
        Long limiter;
        List<String> keyList = new ArrayList<>();
        // 设置key值为注解中的值
        keyList.add(limitKey);
        limiter = (Long) redisTemplate.execute(getRedisScript, keyList, expireTime, limitTimes);

        if (null == limiter || limiter == 0) {
            String msg = "由于超过单位时间=" + expireTime + "-允许的请求次数=" + limitTimes + "[触发限流]";
            log.info("触发限流 msg:{}", msg);
            return false;
        }
        boolean result = false;
        try {
            result = (boolean) jp.proceed();
        } catch (Throwable throwable) {
            log.error("限流方法执行失败！message: {}", throwable);
        }
        return result;
    }

    private String getRateLimiterKey(String token) {
        return MessageFormat.format(CacheNameSpace.Notification.LOCK_RATE_LIMITER, token);
    }
}
