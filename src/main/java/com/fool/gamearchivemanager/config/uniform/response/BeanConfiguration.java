package com.fool.gamearchivemanager.config.uniform.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.Collections;

@Configuration("uniform-response-bean-configuration")
public class BeanConfiguration {

    @Bean("uniform-response-http-message-converter")
    public MappingJackson2HttpMessageConverter jacksonHttpMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();


        MediaType mediaType = new MediaType("application", "json", 1.0);

        ArrayList<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(mediaType);
        httpMessageConverter.setSupportedMediaTypes(Collections.unmodifiableList(mediaTypes));


        httpMessageConverter.setObjectMapper(objectMapper);
        return httpMessageConverter;
    }


}
