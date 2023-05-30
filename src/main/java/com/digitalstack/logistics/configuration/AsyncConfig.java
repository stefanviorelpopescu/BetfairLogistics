package com.digitalstack.logistics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class AsyncConfig
{
    @Bean
    public ThreadPoolExecutor shippingExecutor() {
        int corePoolSize = 4;
        int maximumPoolSize = 4;
        long keepAliveTime = 0;
        TimeUnit unit = TimeUnit.SECONDS; //the time unit for the {@code keepAliveTime} argument
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ThreadPoolExecutor shippingExecutorBackup() {
        int corePoolSize = 4;
        int maximumPoolSize = 4;
        long keepAliveTime = 0;
        TimeUnit unit = TimeUnit.SECONDS; //the time unit for the {@code keepAliveTime} argument
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new ThreadPoolExecutor.AbortPolicy());
    }

}
