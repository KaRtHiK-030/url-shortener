package com.karthik.urlshortener.service;

import com.karthik.urlshortener.dto.FullUrl;
import com.karthik.urlshortener.dto.ShortUrl;
import com.karthik.urlshortener.dto.UrlAnalyticsResponse;
import com.karthik.urlshortener.dto.UrlResponse;
import com.karthik.urlshortener.entity.UrlEntity;
import com.karthik.urlshortener.exception.ShortUrlNotFoundException;
import com.karthik.urlshortener.exception.UrlExpiredException;
import com.karthik.urlshortener.repository.UrlRepository;
import com.karthik.urlshortener.util.ShorteningUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void shouldThrowUrlExpiredException() {

        UrlEntity entity = new UrlEntity();
        entity.setId(1L);
        entity.setFullUrl("https://google.com");
        entity.setShortUrl("abc123");
        entity.setClickCount(5L);
        entity.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(urlRepository.findByShortUrl("abc123"))
                .thenReturn(Optional.of(entity));

        assertThrows(
                UrlExpiredException.class,
                () -> urlService.getFullUrl("abc123")
        );

        verify(urlRepository).findByShortUrl("abc123");
        verify(urlRepository, never()).save(any());
    }

    @Test
    void shouldReturnExistingCustomAliasWhenUrlAlreadyExists() {

        FullUrl request = new FullUrl();
        request.setFullUrl("https://spring.io");

        UrlEntity entity = new UrlEntity();
        entity.setFullUrl(request.getFullUrl());
        entity.setCustomAlias("spring");

        when(urlRepository.findByFullUrl(request.getFullUrl()))
                .thenReturn(List.of(entity));

        ShortUrl response = urlService.getShortUrl(request);

        assertEquals("spring", response.getShortUrl());

        verify(urlRepository).findByFullUrl(request.getFullUrl());
        verify(urlRepository, never()).save(any());
    }

    @Test
    void shouldGenerateNewShortUrlWhenExistingUrlIsExpired() {

        FullUrl request = new FullUrl("https://google.com");

        UrlEntity expired = new UrlEntity();
        expired.setId(1L);
        expired.setFullUrl(request.getFullUrl());
        expired.setShortUrl("old");
        expired.setExpiresAt(LocalDateTime.now().minusDays(2));

        when(urlRepository.findByFullUrl(request.getFullUrl()))
                .thenReturn(List.of(expired));

        UrlEntity firstSave = new UrlEntity();
        firstSave.setId(100L);

        UrlEntity secondSave = new UrlEntity();
        secondSave.setId(100L);
        secondSave.setShortUrl(ShorteningUtil.idToStr(100L));

        when(urlRepository.save(any()))
                .thenReturn(firstSave)
                .thenReturn(secondSave);

        ShortUrl response = urlService.getShortUrl(request);

        assertEquals(ShorteningUtil.idToStr(100L), response.getShortUrl());

        verify(urlRepository).findByFullUrl(request.getFullUrl());
        verify(urlRepository, times(2)).save(any());
    }

    @Test
    void shouldIncrementClickCountWhenNull() {

        UrlEntity entity = new UrlEntity();
        entity.setId(1L);
        entity.setFullUrl("https://google.com");
        entity.setShortUrl("abc");
        entity.setClickCount(null);

        when(urlRepository.findByShortUrl("abc"))
                .thenReturn(Optional.of(entity));

        FullUrl response = urlService.getFullUrl("abc");

        assertEquals("https://google.com", response.getFullUrl());
        assertEquals(1L, entity.getClickCount());
        assertNotNull(entity.getLastAccessedAt());

        ArgumentCaptor<UrlEntity> captor = ArgumentCaptor.forClass(UrlEntity.class);
        verify(urlRepository).save(captor.capture());

        UrlEntity saved = captor.getValue();
        assertEquals(1L, saved.getClickCount());
        assertNotNull(saved.getLastAccessedAt());
    }

    @Test
    void shouldReturnAnalytics() {

        UrlEntity entity = new UrlEntity();
        entity.setId(5L);
        entity.setFullUrl("https://google.com");
        entity.setShortUrl("abc");
        entity.setCustomAlias("google");
        entity.setClickCount(25L);
        entity.setCreatedAt(LocalDateTime.now().minusDays(5));
        entity.setLastAccessedAt(LocalDateTime.now());

        when(urlRepository.findById(5L))
                .thenReturn(Optional.of(entity));

        UrlAnalyticsResponse response = urlService.getAnalytics(5L);

        assertEquals(5L, response.getId());
        assertEquals("https://google.com", response.getFullUrl());
        assertEquals("abc", response.getShortUrl());
        assertEquals("google", response.getCustomAlias());
        assertEquals(25L, response.getClickCount());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getLastAccessedAt());

        verify(urlRepository).findById(5L);
    }

    @Test
    void shouldReturnAllUrls() {

        UrlEntity one = new UrlEntity(1L, "https://google.com", "abc", null);
        UrlEntity two = new UrlEntity(2L, "https://spring.io", "xyz", "spring");

        when(urlRepository.findAll())
                .thenReturn(List.of(one, two));

        List<UrlResponse> urls = urlService.getAllUrls();

        assertEquals(2, urls.size());
        assertEquals("https://google.com", urls.get(0).getFullUrl());
        assertEquals("spring", urls.get(1).getCustomAlias());

        verify(urlRepository).findAll();
    }

    @Test
    void shouldThrowShortUrlNotFoundException() {

        when(urlRepository.findByShortUrl("missing"))
                .thenReturn(Optional.empty());
        when(urlRepository.findByCustomAlias("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                ShortUrlNotFoundException.class,
                () -> urlService.getFullUrl("missing")
        );

        verify(urlRepository).findByShortUrl("missing");
        verify(urlRepository).findByCustomAlias("missing");
        verify(urlRepository, never()).save(any());
    }

    @Test
    void shouldCreateShortUrlWhenUrlDoesNotExist() {

        FullUrl request = new FullUrl("https://newsite.com");

        when(urlRepository.findByFullUrl(request.getFullUrl()))
                .thenReturn(List.of());

        UrlEntity firstSave = new UrlEntity();
        firstSave.setId(42L);

        UrlEntity secondSave = new UrlEntity();
        secondSave.setId(42L);
        secondSave.setShortUrl(ShorteningUtil.idToStr(42L));

        when(urlRepository.save(any()))
                .thenReturn(firstSave)
                .thenReturn(secondSave);

        ShortUrl response = urlService.getShortUrl(request);

        assertEquals(ShorteningUtil.idToStr(42L), response.getShortUrl());

        verify(urlRepository).findByFullUrl(request.getFullUrl());
        verify(urlRepository, times(2)).save(any());
    }

    @Test
    void shouldReturnExistingShortUrlWhenUrlAlreadyExists() {

        FullUrl request = new FullUrl("https://spring.io");

        UrlEntity existing = new UrlEntity();
        existing.setFullUrl(request.getFullUrl());
        existing.setShortUrl("abc123");
        existing.setExpiresAt(LocalDateTime.now().plusDays(1)); // not expired

        when(urlRepository.findByFullUrl(request.getFullUrl()))
                .thenReturn(List.of(existing));

        ShortUrl response = urlService.getShortUrl(request);

        assertEquals("abc123", response.getShortUrl());

        verify(urlRepository).findByFullUrl(request.getFullUrl());
        verify(urlRepository, never()).save(any());
    }

    @Test
    void shouldUpdateLastAccessedAt() {

        UrlEntity entity = new UrlEntity();
        entity.setId(1L);
        entity.setFullUrl("https://google.com");
        entity.setShortUrl("abc123");
        entity.setClickCount(3L);
        entity.setLastAccessedAt(null);

        when(urlRepository.findByShortUrl("abc123"))
                .thenReturn(Optional.of(entity));

        LocalDateTime before = LocalDateTime.now();

        urlService.getFullUrl("abc123");

        assertNotNull(entity.getLastAccessedAt());
        assertTrue(entity.getLastAccessedAt().isAfter(before)
                || entity.getLastAccessedAt().isEqual(before));

        verify(urlRepository).findByShortUrl("abc123");
        verify(urlRepository).save(entity);
    }

    @Test
    void shouldPersistExpirationDate() {

        LocalDateTime expiry = LocalDateTime.now().plusDays(7);

        FullUrl request = new FullUrl("https://newsite.com");
        request.setExpiresAt(expiry);

        when(urlRepository.findByFullUrl(request.getFullUrl()))
                .thenReturn(List.of());

        UrlEntity firstSave = new UrlEntity();
        firstSave.setId(55L);
        firstSave.setExpiresAt(expiry);

        UrlEntity secondSave = new UrlEntity();
        secondSave.setId(55L);
        secondSave.setExpiresAt(expiry);
        secondSave.setShortUrl(ShorteningUtil.idToStr(55L));

        ArgumentCaptor<UrlEntity> captor = ArgumentCaptor.forClass(UrlEntity.class);

        when(urlRepository.save(any()))
                .thenReturn(firstSave)
                .thenReturn(secondSave);

        urlService.getShortUrl(request);

        verify(urlRepository, times(2)).save(captor.capture());

        UrlEntity firstCapturedSave = captor.getAllValues().get(0);

        assertEquals(expiry, firstCapturedSave.getExpiresAt());

        verify(urlRepository).findByFullUrl(request.getFullUrl());
    }

    @Test
    void shouldFindByCustomAliasWhenShortUrlNotFound() {

        UrlEntity entity = new UrlEntity();
        entity.setId(9L);
        entity.setFullUrl("https://aliasedsite.com");
        entity.setShortUrl("aliasCode");
        entity.setCustomAlias("aliasCode");
        entity.setClickCount(0L);

        when(urlRepository.findByShortUrl("aliasCode"))
                .thenReturn(Optional.empty());
        when(urlRepository.findByCustomAlias("aliasCode"))
                .thenReturn(Optional.of(entity));

        FullUrl response = urlService.getFullUrl("aliasCode");

        assertEquals("https://aliasedsite.com", response.getFullUrl());
        assertEquals(1L, entity.getClickCount());
        assertNotNull(entity.getLastAccessedAt());

        verify(urlRepository).findByShortUrl("aliasCode");
        verify(urlRepository).findByCustomAlias("aliasCode");
        verify(urlRepository).save(entity);
    }

    @Test
    void shouldCreateShortUrlWithCustomAlias() {

        FullUrl request = new FullUrl("https://mysite.com");
        request.setCustomAlias("mysite");

        when(urlRepository.findByFullUrl(request.getFullUrl()))
                .thenReturn(List.of());
        when(urlRepository.findByCustomAlias("mysite"))
                .thenReturn(Optional.empty());

        UrlEntity saved = new UrlEntity();
        saved.setId(7L);
        saved.setCustomAlias("mysite");
        saved.setShortUrl("mysite");

        when(urlRepository.save(any()))
                .thenReturn(saved);

        ShortUrl response = urlService.getShortUrl(request);

        assertEquals("mysite", response.getShortUrl());

        verify(urlRepository).findByFullUrl(request.getFullUrl());
        verify(urlRepository).findByCustomAlias("mysite");
        verify(urlRepository, times(1)).save(any()); // custom alias path saves only once
    }

    @Test
    void shouldThrowExceptionWhenCustomAliasAlreadyExists() {

        FullUrl request = new FullUrl("https://mysite.com");
        request.setCustomAlias("taken");

        when(urlRepository.findByFullUrl(request.getFullUrl()))
                .thenReturn(List.of());

        UrlEntity existingAlias = new UrlEntity();
        existingAlias.setCustomAlias("taken");

        when(urlRepository.findByCustomAlias("taken"))
                .thenReturn(Optional.of(existingAlias));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> urlService.getShortUrl(request)
        );

        verify(urlRepository).findByFullUrl(request.getFullUrl());
        verify(urlRepository).findByCustomAlias("taken");
        verify(urlRepository, never()).save(any());
    }

    @Test
    void shouldGenerateNewShortUrlWhenExistingCustomAliasIsExpired() {

        FullUrl request = new FullUrl("https://oldsite.com");

        UrlEntity expiredWithAlias = new UrlEntity();
        expiredWithAlias.setFullUrl(request.getFullUrl());
        expiredWithAlias.setCustomAlias("oldalias");
        expiredWithAlias.setShortUrl("oldalias");
        expiredWithAlias.setExpiresAt(LocalDateTime.now().minusDays(1)); // expired

        when(urlRepository.findByFullUrl(request.getFullUrl()))
                .thenReturn(List.of(expiredWithAlias));

        UrlEntity firstSave = new UrlEntity();
        firstSave.setId(200L);

        UrlEntity secondSave = new UrlEntity();
        secondSave.setId(200L);
        secondSave.setShortUrl(ShorteningUtil.idToStr(200L));

        when(urlRepository.save(any()))
                .thenReturn(firstSave)
                .thenReturn(secondSave);

        ShortUrl response = urlService.getShortUrl(request);

        // Since expired, falls through to base62 generation, not alias reuse
        assertEquals(ShorteningUtil.idToStr(200L), response.getShortUrl());

        verify(urlRepository).findByFullUrl(request.getFullUrl());
        verify(urlRepository, times(2)).save(any());
    }
}