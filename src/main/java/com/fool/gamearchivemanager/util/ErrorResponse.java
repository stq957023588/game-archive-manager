package com.fool.gamearchivemanager.util;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorResponse<T> implements Response<T> {

    private int code;

    private String message;

    private T data;

    private Date time;

    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(int code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.time = new Date();
    }
}
