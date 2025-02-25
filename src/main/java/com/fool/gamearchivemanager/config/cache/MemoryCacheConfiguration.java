package com.fool.gamearchivemanager.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(name = "cache.type", havingValue = "MEMORY")
public class MemoryCacheConfiguration {

    @Bean
    CacheTemplate memoryCacheTemplate(){
        return new MemoryCacheTemplate();
    }

}
