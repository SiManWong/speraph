package com.nageoffer.speraph.demo.config;

import com.nageoffer.speraph.core.ApplicationContextHolder;
import com.nageoffer.speraph.core.IdempotentAspect;
import com.nageoffer.speraph.core.IdempotentExecuteHandler;
import com.nageoffer.speraph.http.IdempotentParamExecuteHandler;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SperaphConfig {

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public IdempotentExecuteHandler idempotentExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentParamExecuteHandler(redissonClient);
    }

    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

}
