package per.yan.ding.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import per.yan.ding.util.lock.RedisDistributeLockHelper;

import java.nio.charset.StandardCharsets;

/**
 * @author xuyg
 * @create 2018-08-31 11:50
 **/
@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        return new RedisCacheManager(redisTemplate);
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer(StandardCharsets.UTF_8);
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setKeySerializer(stringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        template.setKeySerializer(stringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisDistributeLockHelper redisDistributeLockHelper(@Qualifier("stringRedisTemplate") StringRedisTemplate redisTemplate) {
        RedisDistributeLockHelper lockHelper = new RedisDistributeLockHelper("ovc-notification", redisTemplate);
        lockHelper.setMaxLockTimeSeconds(30);
        return lockHelper;
    }
}
