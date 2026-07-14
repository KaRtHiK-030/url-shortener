package com.karthik.urlshortener.service;

import com.karthik.urlshortener.dto.FullUrl;
import com.karthik.urlshortener.dto.ShortUrl;
import com.karthik.urlshortener.entity.UrlEntity;
import com.karthik.urlshortener.repository.UrlRepository;
import com.karthik.urlshortener.util.ShorteningUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /**
     * Retrieves a URL entity by its database ID.
     *
     * @param id database ID
     * @return URL entity
     */
    private UrlEntity get(Long id) {

        logger.info("Fetching URL from database for ID {}", id);

        return urlRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("URL not found with ID: " + id));
    }

    /**
     * Converts a Base62 short code into the original URL.
     *
     * @param shortCode Base62 encoded short code
     * @return original URL
     */
    public FullUrl getFullUrl(String shortCode) {

        logger.debug("Converting Base62 '{}' to database ID", shortCode);

        Long id = ShorteningUtil.strToId(shortCode);

        logger.info("Converted '{}' to ID {}", shortCode, id);

        UrlEntity urlEntity = get(id);

        return new FullUrl(urlEntity.getFullUrl());
    }

    /**
     * Saves a URL in the database.
     *
     * @param fullUrl original URL
     * @return saved entity
     */
    private UrlEntity save(FullUrl fullUrl) {

        logger.info("Saving URL to database");

        return urlRepository.save(new UrlEntity(fullUrl.getFullUrl()));
    }

    /**
     * Creates or retrieves a shortened URL.
     *
     * @param fullUrl original URL
     * @return shortened URL
     */
    public ShortUrl getShortUrl(FullUrl fullUrl) {

        logger.info("Checking whether URL already exists");

        List<UrlEntity> savedUrls = checkFullUrlAlreadyExists(fullUrl);

        UrlEntity savedUrl;

        if (savedUrls.isEmpty()) {

            savedUrl = save(fullUrl);

            logger.debug("Saved entity: {}", savedUrl);

        } else {

            savedUrl = savedUrls.get(0);

            logger.info("URL already exists. Reusing existing record.");
        }

        String shortUrlText = ShorteningUtil.idToStr(savedUrl.getId());

        logger.info(
                "Generated short code '{}' for ID {}",
                shortUrlText,
                savedUrl.getId()
        );

        return new ShortUrl(shortUrlText);
    }

    /**
     * Checks whether a URL already exists in the database.
     *
     * @param fullUrl original URL
     * @return matching URL entities
     */
    private List<UrlEntity> checkFullUrlAlreadyExists(FullUrl fullUrl) {

        return urlRepository.findByFullUrl(fullUrl.getFullUrl());
    }
}