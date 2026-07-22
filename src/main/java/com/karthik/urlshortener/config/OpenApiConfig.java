package com.karthik.urlshortener.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI urlShortenerOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("URL Shortener REST API")
                        .description("""
                                A production-ready URL Shortener application built using
                                Spring Boot 3, Java 21, Spring Data JPA, and MySQL.

                                Features:
                                • URL Shortening
                                • URL Redirection
                                • Custom Alias
                                • URL Expiration
                                • QR Code Generation
                                • Click Analytics
                                • Duplicate URL Detection
                                • RESTful APIs
                                • OpenAPI Documentation
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Karthik Naik")
                                .email("YOUR_EMAIL_HERE"))
                        .license(new License()
                                .name("MIT License")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server"))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("https://github.com/KaRtHiK-030/URL_Shortner"));
    }
}