package com.qfqg_es.config;

import com.qfqg_es.helper.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManagerConfig(){
        return CacheManager.getCacheManager();
    }
}
