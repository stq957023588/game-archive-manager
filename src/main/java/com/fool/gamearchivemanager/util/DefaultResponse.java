package com.fool.gamearchivemanager.util;

import lombok.Data;

@Data
public class DefaultResponse <T> implements Response<T>{

    private int code;

    private String message;

    private T data;

    public DefaultResponse(T data) {
        this.data = data;
    }

    public DefaultResponse() {
    }
}
