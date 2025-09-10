package com.example.chatapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    //todo
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .servers(
                        List.of(new Server().url("https://localhost:8080"))
                )
                .info(
                        new Info().title("Team Challenge Chat API")
                );
    }
}
