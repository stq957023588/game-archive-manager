package com.fool.gamearchivemanager.entity.dto;

import lombok.Data;

@Data
public class AccountDTO {

    private Integer uid;

    private String username;

    private String password;


    public AccountDTO(Integer uid, String username, String password) {
        this.uid = uid;
        this.username = username;
        this.password = password;
    }

    public AccountDTO() {
    }
}
