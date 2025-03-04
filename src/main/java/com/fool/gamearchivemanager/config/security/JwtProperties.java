package com.fool.gamearchivemanager.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String issuer = "sixteen chapter";

    private String secret = "123456";

    private Long expiration;


}
