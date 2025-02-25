package com.fool.gamearchivemanager.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.util.Date;

public class UniformResponseUtils {

    public static int SUCCESS = 200;

    public static int UN_AUTHORIZATION = 401;

    public static int FORBIDDEN = 403;

    public static int ERROR = 500;

    public static Response<?> success(String message) {
        DefaultResponse<Object> result = new DefaultResponse<>();
        result.setCode(SUCCESS);
        result.setMessage(message);
        return result;
    }

    public static <O> Response<O> success(String message, O data) {
        DefaultResponse<O> result = new DefaultResponse<>();
        result.setCode(SUCCESS);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static Response<?> error(String message) {
        DefaultResponse<?> result = new DefaultResponse<>();
        result.setCode(ERROR);
        result.setMessage(message);
        return result;
    }

    public static Response<String> loginSuccess(String data, Date expireAt) {
        TokenResponse result = new TokenResponse();
        result.setData(data);
        result.setExpireAt(expireAt);
        return result;
    }

    public static Response<?> loginFailure(AuthenticationException e) {
        return new ErrorResponse<>(FORBIDDEN, e.getLocalizedMessage(), null);
    }

    public static Response<Object> unAuthorization(AuthenticationException e) {
        return new ErrorResponse<>(UN_AUTHORIZATION, e.getLocalizedMessage(), null);
    }

    public static Response<Object> accessDenied(AccessDeniedException e) {
        return new ErrorResponse<>(UN_AUTHORIZATION, e.getLocalizedMessage(), null);
    }


}
