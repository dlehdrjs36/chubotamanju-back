package com.side.chubotamanju.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); //스레드 풀 기본 스레드 수
        executor.setMaxPoolSize(5); // 스레드 풀 최대 스레드 수
        executor.setQueueCapacity(500); // 작업 대기열 크기(스레드가 꽉 차면 500개까지 대기열)
        executor.setThreadNamePrefix("AsyncThread-"); //스레드 접두사
        executor.initialize();
        return executor;
    }
}
