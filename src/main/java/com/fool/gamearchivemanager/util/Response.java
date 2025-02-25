package com.fool.gamearchivemanager.util;

import java.io.Serializable;

public interface Response<T> extends Serializable {

    void setCode(int code);

    int getCode();

    void setMessage(String message);

    String getMessage();

    void setData(T data);

    T getData();

}
