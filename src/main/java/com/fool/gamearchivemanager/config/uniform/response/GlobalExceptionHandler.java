package com.fool.gamearchivemanager.config.uniform.response;

import com.fool.gamearchivemanager.util.Response;
import com.fool.gamearchivemanager.util.UniformResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.UUID;


@Slf4j
@RestControllerAdvice(basePackages = "com.fool")
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response<?> handleException(Exception e, HttpServletRequest request) {
        log.error("Path:{}", request.getServletPath(), e);
        return UniformResponseUtils.error(e.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        HashMap<String, String> map = new HashMap<>();

        while (true) {
            map.put(UUID.randomUUID().toString(), "AA");
            Thread.sleep(1000L);
            System.out.println(map.size());
        }
    }

}
