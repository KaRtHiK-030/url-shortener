package com.karthik.urlshortener.controller;

import com.karthik.urlshortener.dto.FullUrl;
import com.karthik.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
public class RedirectController {

    private static final Logger logger =
            LoggerFactory.getLogger(RedirectController.class);

    private final UrlService urlService;

    public RedirectController(UrlService urlService) {
        this.urlService = urlService;

        System.out.println(">>>>>>>> RedirectController Loaded <<<<<<<<");
    }

    @GetMapping("/{shortCode}")
    public void redirect(
            @PathVariable String shortCode,
            HttpServletResponse response) {

        System.out.println("==================================================");
        System.out.println("Received shortCode = " + shortCode);
        System.out.println("==================================================");

        try {

            FullUrl fullUrl = urlService.getFullUrl(shortCode);

            System.out.println("Found URL = " + fullUrl.getFullUrl());

            logger.info("Redirecting to {}", fullUrl.getFullUrl());

            response.sendRedirect(fullUrl.getFullUrl());

        } catch (NoSuchElementException e) {

            System.out.println("NoSuchElementException:");
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "URL not found",
                    e
            );

        } catch (IOException e) {

            System.out.println("IOException:");
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to redirect",
                    e
            );

        } catch (IllegalStateException e) {

            System.out.println("IllegalStateException:");
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    e.getMessage(),
                    e
            );

        } catch (Exception e) {

            System.out.println("Unexpected Exception:");
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error",
                    e
            );
        }
    }
}