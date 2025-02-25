package com.fool.gamearchivemanager.module.common.controller;

import com.fool.gamearchivemanager.config.cache.CacheTemplate;
import com.fool.gamearchivemanager.util.SecurityContextUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class IdempotentController {

    private final CacheTemplate cacheTemplate;

    public IdempotentController(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    @GetMapping("/single-use/token")
    public String requireSingleUseToken() {
        String username = SecurityContextUtils.getUsername();
        String token = UUID.randomUUID().toString();
        cacheTemplate.put(token, username);

        return token;
    }

}
