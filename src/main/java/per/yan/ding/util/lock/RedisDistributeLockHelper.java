package per.yan.ding.util.lock;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author gaoyan
 * @date 2019/2/26 19:56
 */
@Getter
@Setter
public class RedisDistributeLockHelper implements DistributeLockHelper {
    private String applicationCode;
    private Integer maxLockTimeSeconds = 60;
    private StringRedisTemplate redisTemplate;

    public RedisDistributeLockHelper(String applicationCode, StringRedisTemplate redisTemplate) {
        this.applicationCode = applicationCode;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public DistributeLockHolder getLock(String hashCode) {
        final DistributeLockHolder lockHolder = this.generateLockHolder(hashCode);
        boolean result = this.redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.setNX(lockHolder.getLock().getBytes(), "true".getBytes())
        );
        if (result) {
            this.redisTemplate.expire(lockHolder.getLock(), (long) this.maxLockTimeSeconds, TimeUnit.SECONDS);
            return lockHolder;
        } else {
            return null;
        }
    }

    @Override
    public Boolean isLocked(String hashCode) {
        DistributeLockHolder lockHolder = this.generateLockHolder(hashCode);
        return this.redisTemplate.opsForValue().get(lockHolder.getLock()) != null;
    }

    @Override
    public void release(DistributeLockHolder lockHolder) {
        if (lockHolder != null) {
            this.redisTemplate.delete(lockHolder.getLock());
        }

    }

    private DistributeLockHolder generateLockHolder(String hashCode) {
        DistributeLockHolder lockHolder = new DistributeLockHolder();
        final String keyLockTemplate = "RedisLock-aCode:%s-hCode:%s";
        lockHolder.setLock(String.format(keyLockTemplate, this.applicationCode, hashCode));
        return lockHolder;
    }
}
