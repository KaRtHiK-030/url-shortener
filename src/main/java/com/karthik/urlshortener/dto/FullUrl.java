package com.karthik.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class FullUrl {

    @NotBlank(message = "URL cannot be empty")
    @Size(max = 2048, message = "URL is too long")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "URL must start with http:// or https://"
    )
    private String fullUrl;

    @Size(max = 50, message = "Custom alias must not exceed 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]*$",
            message = "Custom alias can contain only letters, numbers, hyphens (-) and underscores (_)"
    )
    private String customAlias;

    /**
     * Optional expiration date and time.
     * If null, the URL never expires.
     */
    private LocalDateTime expiresAt;

    public FullUrl() {
    }

    public FullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public FullUrl(String fullUrl, String customAlias) {
        this.fullUrl = fullUrl;
        this.customAlias = customAlias;
    }

    public FullUrl(String fullUrl,
                   String customAlias,
                   LocalDateTime expiresAt) {
        this.fullUrl = fullUrl;
        this.customAlias = customAlias;
        this.expiresAt = expiresAt;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "FullUrl{" +
                "fullUrl='" + fullUrl + '\'' +
                ", customAlias='" + customAlias + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}