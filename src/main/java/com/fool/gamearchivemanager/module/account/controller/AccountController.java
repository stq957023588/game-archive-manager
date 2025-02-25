package com.fool.gamearchivemanager.module.account.controller;

import com.fool.gamearchivemanager.entity.dto.AccountDTO;
import com.fool.gamearchivemanager.module.account.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/account")
    public void add(@RequestBody AccountDTO dto) {
        accountService.add(dto);
    }

}
