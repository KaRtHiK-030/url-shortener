package com.karthik.urlshortener.controller;

import com.google.zxing.WriterException;
import com.karthik.urlshortener.dto.FullUrl;
import com.karthik.urlshortener.dto.ShortUrl;
import com.karthik.urlshortener.dto.UrlAnalyticsResponse;
import com.karthik.urlshortener.dto.UrlResponse;
import com.karthik.urlshortener.service.UrlService;
import com.karthik.urlshortener.util.UrlUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

@Tag(
        name = "URL Shortener API",
        description = "REST APIs for creating, managing, redirecting, generating QR codes and viewing analytics for shortened URLs."
)
@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private static final Logger logger =
            LoggerFactory.getLogger(UrlController.class);

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Creates a shortened URL.
     */
    @Operation(
            summary = "Create Short URL",
            description = "Creates a shortened URL with optional custom alias and expiration date.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Original URL with optional custom alias and expiration date.",
                    required = true
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Short URL created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ShortUrl> shortenUrl(
            @Valid
            @RequestBody FullUrl fullUrl,
            HttpServletRequest request) {

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

        shortUrl.setShortUrl(baseUrl + "/" + shortUrl.getShortUrl());

        logger.info("Short URL created successfully: {}",
                shortUrl.getShortUrl());

        return ResponseEntity
                .created(URI.create(shortUrl.getShortUrl()))
                .body(shortUrl);
    }

    /**
     * Returns all shortened URLs.
     */
    @Operation(
            summary = "Get All URLs",
            description = "Returns all shortened URLs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URLs retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<UrlResponse>> getAllUrls() {

        logger.info("Fetching all URLs");

        List<UrlResponse> urls = urlService.getAllUrls();

        logger.info("Fetched {} URLs", urls.size());

        return ResponseEntity.ok(urls);
    }

    /**
     * Returns analytics for a shortened URL.
     */
    @Operation(
            summary = "Get URL Analytics",
            description = "Returns click count, creation time and last accessed time for a URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @GetMapping("/{id}/analytics")
    public ResponseEntity<UrlAnalyticsResponse> getAnalytics(
            @Parameter(
                    description = "ID of the shortened URL",
                    example = "1"
            )
            @PathVariable Long id) {

        logger.info("Fetching analytics for URL ID {}", id);

        UrlAnalyticsResponse analytics = urlService.getAnalytics(id);

        return ResponseEntity.ok(analytics);
    }

    /**
     * Generates QR Code for a shortened URL.
     */
    @Operation(
            summary = "Generate QR Code",
            description = "Generates a PNG QR Code for the shortened URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QR Code generated successfully"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "410", description = "URL has expired")
    })
    @GetMapping(
            value = "/{shortCode}/qr",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> generateQrCode(
            @Parameter(
                    description = "Short URL code or custom alias",
                    example = "abc123"
            )
            @PathVariable String shortCode,
            HttpServletRequest request) {

        try {

            String baseUrl =
                    UrlUtil.getBaseUrl(request.getRequestURL().toString());

            byte[] qrCode =
                    urlService.generateQrCode(shortCode, baseUrl);

            logger.info("QR Code generated for '{}'", shortCode);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + shortCode + ".png\""
                    )
                    .body(qrCode);

        } catch (MalformedURLException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request URL",
                    e
            );

        } catch (WriterException | IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate QR Code",
                    e
            );
        }
    }

    /**
     * Redirects a shortened URL to its original URL.
     */
    @Operation(
            summary = "Redirect Short URL",
            description = "Redirects a short URL or custom alias to its original destination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirect successful"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "410", description = "URL has expired")
    })
    @GetMapping("/{shortCode}")
    public void redirectToFullUrl(
            @Parameter(
                    description = "Short URL code or custom alias",
                    example = "abc123"
            )
            @PathVariable String shortCode,
            HttpServletResponse response) {

        try {

            FullUrl fullUrl = urlService.getFullUrl(shortCode);

            logger.info("Redirecting to {}", fullUrl.getFullUrl());

            response.sendRedirect(fullUrl.getFullUrl());

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to redirect",
                    e
            );
        }
    }
}