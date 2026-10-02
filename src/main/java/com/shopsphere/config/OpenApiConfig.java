package com.shopsphere.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shopsphereOpenAPI() {

        Server localServer = new Server().url("http://localhost:8080").description("Local Development Server");

        return new OpenAPI().info(new Info().title("ShopSphere API").version("1.0").description("REST API for the ShopSphere e-commerce backend application."))
                .servers(List.of(localServer))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Enter your JWT access token")
                                )
                );
    }
}

