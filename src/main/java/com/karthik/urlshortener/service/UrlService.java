package com.karthik.urlshortener.service;

import com.google.zxing.WriterException;
import com.karthik.urlshortener.dto.FullUrl;
import com.karthik.urlshortener.dto.ShortUrl;
import com.karthik.urlshortener.dto.UrlAnalyticsResponse;
import com.karthik.urlshortener.dto.UrlResponse;
import com.karthik.urlshortener.entity.UrlEntity;
import com.karthik.urlshortener.exception.DuplicateAliasException;
import com.karthik.urlshortener.exception.ShortUrlNotFoundException;
import com.karthik.urlshortener.exception.UrlExpiredException;
import com.karthik.urlshortener.repository.UrlRepository;
import com.karthik.urlshortener.util.QrCodeUtil;
import com.karthik.urlshortener.util.ShorteningUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UrlService {

    private static final Logger logger =
            LoggerFactory.getLogger(UrlService.class);

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /**
     * Retrieves a URL entity by its database ID.
     */
    private UrlEntity get(Long id) {

        logger.info("Fetching URL from database for ID {}", id);

        return urlRepository.findById(id)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException(id.toString()));
    }

    /**
     * Returns the original URL for a given short code
     * and updates click analytics.
     */
    public FullUrl getFullUrl(String shortCode) {

        UrlEntity urlEntity = urlRepository
                .findByShortUrl(shortCode)
                .or(() -> urlRepository.findByCustomAlias(shortCode))
                .orElseThrow(() -> {
                    logger.warn("Short URL not found: {}", shortCode);
                    return new ShortUrlNotFoundException(shortCode);
                });

        // --------------------------
        // Expiration Check
        // --------------------------
        if (urlEntity.getExpiresAt() != null &&
                LocalDateTime.now().isAfter(urlEntity.getExpiresAt())) {

            logger.warn("Expired URL accessed: {}", shortCode);
            throw new UrlExpiredException(shortCode);
        }

        // --------------------------
        // Update Analytics
        // --------------------------
        Long clickCount = urlEntity.getClickCount();

        if (clickCount == null) {
            clickCount = 0L;
        }

        urlEntity.setClickCount(clickCount + 1);
        urlEntity.setLastAccessedAt(LocalDateTime.now());

        urlRepository.save(urlEntity);

        logger.info(
                "Short URL '{}' accessed {} time(s)",
                shortCode,
                urlEntity.getClickCount()
        );

        return new FullUrl(urlEntity.getFullUrl());
    }

    /**
     * Returns analytics for a URL.
     */

    public byte[] generateQrCode(String shortCode, String baseUrl)
            throws WriterException, IOException {

        UrlEntity urlEntity = urlRepository
                .findByShortUrl(shortCode)
                .or(() -> urlRepository.findByCustomAlias(shortCode))
                .orElseThrow(() -> {
                    logger.warn("Short URL not found: {}", shortCode);
                    return new ShortUrlNotFoundException(shortCode);
                });

        if (urlEntity.getExpiresAt() != null
                && LocalDateTime.now().isAfter(urlEntity.getExpiresAt())) {

            logger.warn("Expired URL accessed: {}", shortCode);
            throw new UrlExpiredException(shortCode);
        }

        String shortUrl = baseUrl + "/" + shortCode;

        logger.info("Generating QR Code for {}", shortUrl);

        return QrCodeUtil.generateQrCode(shortUrl);
    }

    public UrlAnalyticsResponse getAnalytics(Long id) {

        UrlEntity urlEntity = get(id);

        return new UrlAnalyticsResponse(
                urlEntity.getId(),
                urlEntity.getFullUrl(),
                urlEntity.getShortUrl(),
                urlEntity.getCustomAlias(),
                urlEntity.getClickCount(),
                urlEntity.getCreatedAt(),
                urlEntity.getLastAccessedAt()
        );
    }

    /**
     * Returns all shortened URLs.
     */
    public List<UrlResponse> getAllUrls() {

        logger.info("Fetching all URLs");

        return urlRepository.findAll()
                .stream()
                .map(url -> new UrlResponse(
                        url.getId(),
                        url.getFullUrl(),
                        url.getShortUrl(),
                        url.getCustomAlias()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Saves URL.
     */
    private UrlEntity save(UrlEntity entity) {

        logger.info("Saving URL to database");

        return urlRepository.save(entity);
    }

    /**
     * Creates or retrieves a shortened URL.
     */
    public ShortUrl getShortUrl(FullUrl fullUrl) {

        logger.info("Checking whether URL already exists");

        List<UrlEntity> savedUrls =
                checkFullUrlAlreadyExists(fullUrl);

        if (!savedUrls.isEmpty()) {

            UrlEntity existing = savedUrls.get(0);

            // If existing URL is expired,
            // create a new one instead of reusing it.
            if (existing.getExpiresAt() == null ||
                    LocalDateTime.now().isBefore(existing.getExpiresAt())) {

                if (existing.getCustomAlias() != null &&
                        !existing.getCustomAlias().isBlank()) {

                    return new ShortUrl(existing.getCustomAlias());
                }

                return new ShortUrl(existing.getShortUrl());
            }
        }

        UrlEntity entity = new UrlEntity();

        entity.setFullUrl(fullUrl.getFullUrl());

        // Save expiration date
        entity.setExpiresAt(fullUrl.getExpiresAt());

        // --------------------------
        // Custom Alias
        // --------------------------

        if (fullUrl.getCustomAlias() != null &&
                !fullUrl.getCustomAlias().isBlank()) {

            if (urlRepository.findByCustomAlias(
                    fullUrl.getCustomAlias()).isPresent()) {

                throw new DuplicateAliasException(
                        "Custom alias already exists."
                );
            }

            entity.setCustomAlias(fullUrl.getCustomAlias());
            entity.setShortUrl(fullUrl.getCustomAlias());

            entity = save(entity);

            logger.info(
                    "Created custom alias '{}'",
                    entity.getCustomAlias()
            );

            return new ShortUrl(entity.getShortUrl());
        }

        // --------------------------
        // Base62 Generated URL
        // --------------------------

        entity = save(entity);

        String shortCode =
                ShorteningUtil.idToStr(entity.getId());

        entity.setShortUrl(shortCode);

        entity = save(entity);

        logger.info(
                "Generated short code '{}'",
                shortCode
        );

        return new ShortUrl(shortCode);
    }

    /**
     * Checks whether URL already exists.
     */
    private List<UrlEntity> checkFullUrlAlreadyExists(
            FullUrl fullUrl) {

        return urlRepository.findByFullUrl(
                fullUrl.getFullUrl());
    }
}