package com.fool.gamearchivemanager.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fool.gamearchivemanager.util.Response;
import com.fool.gamearchivemanager.util.UniformResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.ContentType;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CountAuthenticationFailureHandler implements AuthenticationFailureHandler, JSONReturnHandler {

    private final ObjectMapper objectMapper;

    public CountAuthenticationFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CountAuthenticationFailureHandler() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // TODO 记录每个账户的失败次数
        Response<?> responseObject = UniformResponseUtils.loginFailure(exception);
        jsonReturn(response, responseObject);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public HttpStatus httpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
