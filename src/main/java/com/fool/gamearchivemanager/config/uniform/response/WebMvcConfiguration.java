package com.fool.gamearchivemanager.config.uniform.response;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Iterator;
import java.util.List;

@Configuration
@ConditionalOnBean(name = "uniform-response-http-message-converter")
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final HttpMessageConverter<?> httpMessageConverter;

    public WebMvcConfiguration(@Qualifier("uniform-response-http-message-converter") HttpMessageConverter<?> httpMessageConverter) {
        this.httpMessageConverter = httpMessageConverter;
    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter<?> next = iterator.next();
            if (next == httpMessageConverter) {
                iterator.remove();
                break;
            }
        }

        converters.addFirst(httpMessageConverter);
    }

}