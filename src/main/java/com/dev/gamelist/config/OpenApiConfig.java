package com.dev.gamelist.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "GameList API",
        description = "API para gerenciamento de listas de jogos.",
        version = "1.0.0",
        contact = @Contact(
            name = "Thales Reis",
            email = "datascience.will@gmail.com"
        )
    ),
    servers = @Server(
        url = "http://localhost:8080",
        description = "Servidor local"
    )
)
public class OpenApiConfig {
}
