package com.fool.gamearchivemanager.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fool.gamearchivemanager.util.Response;
import com.fool.gamearchivemanager.util.UniformResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.ContentType;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class JWTAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    private final JwtProperties jwtProperties;

    public JWTAuthenticationSuccessHandler(ObjectMapper objectMapper, JwtProperties jwtProperties) {
        this.objectMapper = objectMapper;
        this.jwtProperties = jwtProperties;
    }

    public JWTAuthenticationSuccessHandler(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long current = System.currentTimeMillis();
        Date expireAt = null;
        if (jwtProperties.getExpiration() != null) {
            expireAt = new Date(current * 1000 + jwtProperties.getExpiration());
        }

        // TODO 登录客户端数量限制


        String token = JWT.create().withIssuer(jwtProperties.getIssuer())
                .withIssuedAt(new Date(current))
                .withExpiresAt(expireAt)
                .withClaim(TokenClaimKey.USER_NAME, userDetails.getUsername())
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));


        Response<String> responseObject = UniformResponseUtils.loginSuccess(token, expireAt);
        response.setContentType("application/json;charset=utf-8");
        String s = objectMapper.writeValueAsString(responseObject);
        response.getWriter().write(s);
    }
}
