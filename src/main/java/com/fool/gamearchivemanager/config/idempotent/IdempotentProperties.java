package com.fool.gamearchivemanager.config.idempotent;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "idempotent")
public class IdempotentProperties {

    private String[] urls;

}
