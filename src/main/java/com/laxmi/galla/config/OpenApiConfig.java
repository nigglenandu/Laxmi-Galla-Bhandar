package com.laxmi.galla.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi laxmiGallaApi() {
        return GroupedOpenApi.builder()
                .group("Laxmi Galla Bhandar APIs")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI laxmiGallaOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Laxmi Galla Bhandar API")
                        .description("API documentation for Laxmi Galla Bhandar platform. " +
                                "Includes authentication, product management, orders, and vendor services.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Nanda Kishor Tharu")
                                .email("nigglenandu@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url("http://localhost:8081")
                        .description("Local Development Server"))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url("https://api.laxmigallabhandar.com")
                        .description("Production Server"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}