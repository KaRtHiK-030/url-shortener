package com.karthik.urlshortener.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                                A modern URL Shortener application built using
                                Spring Boot 3, Spring Data JPA, MySQL and Java 25.

                                Features:
                                • URL Shortening
                                • URL Redirection
                                • Duplicate URL Detection
                                • RESTful APIs
                                • OpenAPI Documentation
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Karthik Naik")
                                .email("your-email@example.com"))
                        .license(new License()
                                .name("MIT License")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("https://github.com/your-username/url-shortener"));
    }
}