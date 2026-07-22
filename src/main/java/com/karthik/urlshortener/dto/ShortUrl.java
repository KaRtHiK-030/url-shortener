package com.karthik.urlshortener.dto;

public class ShortUrl {

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