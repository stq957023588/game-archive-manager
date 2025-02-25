package com.fool.gamearchivemanager.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TokenResponse implements Response<String> {

    @JsonIgnore
    private String accessToken;

    @JsonProperty("expire_at")
    private Date expireAt;


    @Override
    public void setCode(int code) {

    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public void setMessage(String message) {

    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void setData(String data) {
        this.accessToken = data;
    }

    @JsonProperty("access_token")
    @Override
    public String getData() {
        return this.accessToken;
    }

}
