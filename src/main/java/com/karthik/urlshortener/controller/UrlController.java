package com.karthik.urlshortener.controller;

import com.karthik.urlshortener.dto.FullUrl;
import com.karthik.urlshortener.dto.ShortUrl;
import com.karthik.urlshortener.exception.InvalidUrlError;
import com.karthik.urlshortener.service.UrlService;
import com.karthik.urlshortener.util.UrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Creates a shortened URL.
     *
     * @param fullUrl request body containing the original URL
     * @param request current HTTP request
     * @return shortened URL
     */
    @PostMapping
    public ResponseEntity<Object> shortenUrl(
            @Valid @RequestBody FullUrl fullUrl,
            HttpServletRequest request) {

        UrlValidator validator = new UrlValidator(
                new String[]{"http", "https"}
        );

        if (!validator.isValid(fullUrl.getFullUrl())) {

            logger.warn("Invalid URL received: {}", fullUrl.getFullUrl());

            InvalidUrlError error = new InvalidUrlError(
                    "url",
                    fullUrl.getFullUrl(),
                    "Invalid URL"
            );

            return ResponseEntity.badRequest().body(error);
        }

        String baseUrl;

        try {
            baseUrl = UrlUtil.getBaseUrl(request.getRequestURL().toString());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request URL",
                    e
            );
        }

        ShortUrl shortUrl = urlService.getShortUrl(fullUrl);

        // Returns:
        // http://localhost:8080/api/v1/urls/{shortCode}
        shortUrl.setShortUrl(baseUrl + "/" + shortUrl.getShortUrl());

        logger.info("Short URL created successfully: {}", shortUrl.getShortUrl());

        return ResponseEntity.ok(shortUrl);
    }

    /**
     * Redirects a shortened URL to its original destination.
     *
     * @param shortCode shortened URL code
     * @param response HTTP response
     */
    @GetMapping("/{shortCode}")
    public void redirectToFullUrl(
            @PathVariable String shortCode,
            HttpServletResponse response) {

        try {

            FullUrl fullUrl = urlService.getFullUrl(shortCode);

            logger.info("Redirecting to {}", fullUrl.getFullUrl());

            response.sendRedirect(fullUrl.getFullUrl());

        } catch (NoSuchElementException e) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "URL not found",
                    e
            );

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to redirect",
                    e
            );
        }
    }
}