package com.karthik.urlshortener.exception;

public class ShortUrlNotFoundException extends RuntimeException {

    public ShortUrlNotFoundException(String shortUrl) {
        super("Short URL '" + shortUrl + "' not found.");
    }
}