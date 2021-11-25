package com.irostub.studya.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processor = Runtime.getRuntime().availableProcessors();
        log.info("Available processors: {}", processor);
        //기본 풀 개수
        executor.setCorePoolSize(processor);
        //최대 풀 개수
        executor.setMaxPoolSize(processor * 2);
        //기본 풀 개수 초과 시 대기열 큐
        executor.setQueueCapacity(50);
        //스레드 이름 prefix
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }
}
