package com.fool.gamearchivemanager.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String issuer = "localhost";

    private String secret = "123";

    private Long expiration = 100000L;


}
