/*
 * Copyright 2020-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fool.gamearchivemanager.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * @author Joe Grandja
 * @author Steve Riesenberg
 * @since 1.1
 */
@Setter
@Configuration
@ConfigurationProperties(prefix = "security")
public class DefaultSecurityConfig {

    private String[] ignores = new String[0];

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        // TODO 登录失败记录次数，登出删除TOKEN缓存
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(ignores).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(
                        formLogin -> formLogin
                                .successHandler(new JWTAuthenticationSuccessHandler(objectMapper, new JwtProperties()))
                                .failureHandler(new CountAuthenticationFailureHandler(objectMapper))
                )
                .exceptionHandling(
                        configurer -> configurer
                                .accessDeniedHandler(new LogAccessDeniedHandler(objectMapper))
                                .authenticationEntryPoint(new LogAuthenticationEntryPoint(objectMapper))
                )
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutSuccessHandler(new EventLogoutSuccessHandler())
                )
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;


        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(new JwtProperties(), objectMapper);
        http.addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class);
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

}
