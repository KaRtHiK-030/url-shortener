package com.karthik.urlshortener.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
public class UrlEntity {

    private Long id;

    @Column(name = "full_url", nullable = false, length = 2048)
    private String fullUrl;

    @Column(name = "short_url", nullable = false, unique = true, length = 50)
    private String shortUrl;

    @Column(name = "custom_alias", unique = true, length = 50)
    private String customAlias;

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    // NEW
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public UrlEntity() {
    }

    public UrlEntity(Long id,
                     String fullUrl,
                     String shortUrl,
                     String customAlias) {
        this.id = id;
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
        this.customAlias = customAlias;
    }

    public UrlEntity(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public UrlEntity(String fullUrl, String shortUrl) {
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
    }

    public UrlEntity(String fullUrl,
                     String shortUrl,
                     String customAlias) {
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
        this.customAlias = customAlias;
    }

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (clickCount == null) {
            clickCount = 0L;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "UrlEntity{" +
                "id=" + id +
                ", fullUrl='" + fullUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", customAlias='" + customAlias + '\'' +
                ", clickCount=" + clickCount +
                ", createdAt=" + createdAt +
                ", lastAccessedAt=" + lastAccessedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}