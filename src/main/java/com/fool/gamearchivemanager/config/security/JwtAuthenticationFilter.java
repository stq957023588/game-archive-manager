package com.fool.gamearchivemanager.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fool.gamearchivemanager.util.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author fool
 * @date 2021/10/21 16:42
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtProperties jwtProperties, ObjectMapper objectMapper) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = null;

        try {
            Map<String, Claim> claims = JWTUtils.parsingToken(jwtProperties.getSecret(), jwtProperties.getIssuer(), token);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Token claims:%s", claims));
            }

            String userName = claims.get(TokenClaimKey.USER_NAME).as(String.class);
            UserDetails userDetails = User.builder().username(userName).password("PROTECTED").roles("").authorities(Collections.singleton(() -> "")).build();

            authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Authenticated user %s, setting security context", ""));
            }
        } catch (JWTVerificationException e) {
            logger.warn(String.format("Verify token failed:%s", token), e);
        } catch (Exception e) {
            logger.error(String.format("Parsing token failed:%s", token), e);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
