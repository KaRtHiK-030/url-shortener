package com.karthik.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ShortUrl {

    @NotBlank(message = "Short URL cannot be empty")
    @Size(max = 50, message = "Short URL must not exceed 50 characters")
    private String shortUrl;

    public ShortUrl() {
    }

    public ShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    /**
     * Returns the shortened URL.
     *
     * @return shortened URL
     */
    public String getShortUrl() {
        return shortUrl;
    }

    /**
     * Sets the shortened URL.
     *
     * @param shortUrl shortened URL
     */
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @Override
    public String toString() {
        return "ShortUrl{" +
                "shortUrl='" + shortUrl + '\'' +
                '}';
    }
}