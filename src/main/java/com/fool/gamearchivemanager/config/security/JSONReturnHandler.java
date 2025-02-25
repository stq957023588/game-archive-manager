package com.fool.gamearchivemanager.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public interface JSONReturnHandler {


    ObjectMapper getObjectMapper();

    HttpStatus httpStatus();

    default void jsonReturn(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(httpStatus().value());

        response.setContentType("application/json;charset=utf-8");
        String s = getObjectMapper().writeValueAsString(data);
        response.getWriter().write(s);
    }

}
