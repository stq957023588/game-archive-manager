package com.fool.gamearchivemanager.config.idempotent;

import com.fool.gamearchivemanager.config.cache.CacheTemplate;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdempotentConfiguration {

    @Bean
    public FilterRegistrationBean<Filter> idempotentFilter(CacheTemplate cacheTemplate, IdempotentProperties properties) {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        filterFilterRegistrationBean.setFilter(new IdempotentFilter(cacheTemplate));
        filterFilterRegistrationBean.addUrlPatterns(properties.getUrls());
        filterFilterRegistrationBean.setName("Idempotent Filter");
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.setEnabled(true);
        return filterFilterRegistrationBean;
    }


}
