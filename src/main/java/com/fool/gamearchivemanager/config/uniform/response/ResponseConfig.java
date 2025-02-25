package com.fool.gamearchivemanager.config.uniform.response;

import com.fool.gamearchivemanager.util.DefaultResponse;
import com.fool.gamearchivemanager.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.fool")
public class ResponseConfig implements ResponseBodyAdvice<Object> {


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (returnType.hasMethodAnnotation(IgnoreUniformResponse.class)) {
            return body;
        }
        if (log.isDebugEnabled()) {
            log.debug("Uniform response!Current response type:{}", body == null ? null : body.getClass());
        }

        if (body instanceof Response<?>) {
            return body;
        }

        return new DefaultResponse<>(body);
    }
}
