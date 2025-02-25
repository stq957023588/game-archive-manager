package com.fool.gamearchivemanager.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fool.gamearchivemanager.util.Response;
import com.fool.gamearchivemanager.util.UniformResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class LogAccessDeniedHandler implements AccessDeniedHandler, JSONReturnHandler {
    private final ObjectMapper objectMapper;

    public LogAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public LogAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String servletPath = request.getServletPath();
        log.error("Access denied,path:{}", servletPath);
        Response<Object> responseObject = UniformResponseUtils.accessDenied(accessDeniedException);
        jsonReturn(response, responseObject);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
