package com.fool.gamearchivemanager.config.idempotent;

import com.fool.gamearchivemanager.config.cache.CacheTemplate;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class IdempotentFilter implements Filter {

    private final CacheTemplate cacheTemplate;

    public IdempotentFilter(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String singleUseTokenHeader = "Single-Use-Token";
        String singleUseToken = httpRequest.getHeader(singleUseTokenHeader);
        if (singleUseToken == null) {
            throw new RuntimeException("缺少一次性令牌");
        }

        Object value = cacheTemplate.get(singleUseToken);
        if (value == null) {
            throw new RuntimeException("无效的一次性令牌");
        }
        cacheTemplate.delete(singleUseToken);
        chain.doFilter(request, response);
    }

}