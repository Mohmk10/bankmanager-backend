package com.bankmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    private String secret = "BankManagerSecretKeyForJWT2025MustBeLongEnoughForHS512Algorithm";
    private long expiration = 86400000;
}