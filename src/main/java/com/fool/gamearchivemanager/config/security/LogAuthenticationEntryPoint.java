package com.fool.gamearchivemanager.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fool.gamearchivemanager.util.Response;
import com.fool.gamearchivemanager.util.UniformResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class LogAuthenticationEntryPoint implements AuthenticationEntryPoint, JSONReturnHandler {

    private final ObjectMapper objectMapper;

    public LogAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public LogAuthenticationEntryPoint() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String servletPath = request.getServletPath();
        log.warn("No authentication,path:{}", servletPath);
        Response<Object> res = UniformResponseUtils.unAuthorization(authException);
        jsonReturn(response, res);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
