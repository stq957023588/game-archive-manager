package com.fool.gamearchivemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class Application {
    public static final String INSTANCE_ID = UUID.randomUUID().toString().replaceAll("-", "");
    // public static final String INSTANCE_ID = "AAAA";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
