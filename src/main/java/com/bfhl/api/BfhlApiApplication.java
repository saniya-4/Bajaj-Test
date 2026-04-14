package com.bfhl.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.bfhl.api.config.BfhlProperties;
import com.bfhl.api.config.DotenvLoader;

@SpringBootApplication
@EnableConfigurationProperties(BfhlProperties.class)
public class BfhlApiApplication {

    public static void main(String[] args) {
        DotenvLoader.loadIfPresent();
        SpringApplication.run(BfhlApiApplication.class, args);
    }
}
