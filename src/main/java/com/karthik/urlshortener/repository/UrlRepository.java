package com.karthik.urlshortener.repository;

import com.karthik.urlshortener.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    /**
     * Finds URLs matching the given original URL.
     *
     * @param fullUrl original URL
     * @return matching URL entities
     */
    List<UrlEntity> findByFullUrl(String fullUrl);

    /**
     * Finds a URL by its generated short code.
     *
     * @param shortUrl generated short code
     * @return matching URL
     */
    Optional<UrlEntity> findByShortUrl(String shortUrl);

    /**
     * Finds a URL by its custom alias.
     *
     * @param customAlias custom alias
     * @return matching URL
     */
    Optional<UrlEntity> findByCustomAlias(String customAlias);
}