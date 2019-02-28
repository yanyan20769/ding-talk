package per.yan.ding.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author gaoyan
 * @date 2018/10/18 09:27
 */
@Configuration
public class ThreadPoolConfig {

    @Value("${thread.pool.core-pool-size:5}")
    private Integer corePoolSize;
    @Value("${thread.pool.max-pool-size:30}")
    private Integer maxPoolSize;
    @Value("${thread.pool.queue-capacity:3000}")
    private Integer queueCapacity;
    @Value("${thread.pool.alive-seconds:60}")
    private Integer aliveSeconds;
    @Value("${thread.pool.thread-name-prefix:thread-ding-}")
    private String threadNamePrefix;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(aliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }
}
