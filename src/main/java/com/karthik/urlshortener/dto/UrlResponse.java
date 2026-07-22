package com.karthik.urlshortener.dto;

public class UrlResponse {

    private Long id;
    private String fullUrl;
    private String shortUrl;
    private String customAlias;

    public UrlResponse() {
    }

    public UrlResponse(Long id,
                       String fullUrl,
                       String shortUrl,
                       String customAlias) {
        this.id = id;
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
        this.customAlias = customAlias;
    }

    public Long getId() {
        return id;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }
}