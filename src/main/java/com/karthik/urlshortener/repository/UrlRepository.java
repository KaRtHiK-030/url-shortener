package com.karthik.urlshortener.repository;

import com.karthik.urlshortener.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    /**
     * Finds URLs matching the given original URL.
     *
     * @param fullUrl original URL
     * @return matching URL entities
     */
    List<UrlEntity> findByFullUrl(String fullUrl);
}