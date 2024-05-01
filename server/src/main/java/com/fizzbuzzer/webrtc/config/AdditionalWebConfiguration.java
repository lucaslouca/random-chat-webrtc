package com.fizzbuzzer.webrtc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class AdditionalWebConfiguration {
    @Autowired
    private Environment env;

    @Bean
    public CacheManager cacheManager() {
        // https://www.baeldung.com/spring-cache-tutorial
        return new ConcurrentMapCacheManager("something-to-cache");
    }
}
