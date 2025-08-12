package com.example.chatapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Data
@Component
public class JwtProperties {
    private String secret;
    private Long expiration;
    private Long refreshExpiration;
    private Long testExpiration;
}
