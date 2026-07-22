package com.karthik.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthik.urlshortener.dto.FullUrl;
import com.karthik.urlshortener.dto.ShortUrl;
import com.karthik.urlshortener.dto.UrlAnalyticsResponse;
import com.karthik.urlshortener.dto.UrlResponse;
import com.karthik.urlshortener.exception.GlobalExceptionHandler;
import com.karthik.urlshortener.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.endsWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
@Import(GlobalExceptionHandler.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    @Test
    @DisplayName("Should create a shortened URL")
    void shouldShortenUrl() throws Exception {

        FullUrl request =
                new FullUrl("https://google.com");

        ShortUrl response =
                new ShortUrl("abc123");

        when(urlService.getShortUrl(any(FullUrl.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr(
                                "jakarta.servlet.forward.request_uri",
                                "/api/v1/urls"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/abc123"))
                .andExpect(jsonPath("$.shortUrl")
                        .value("http://localhost/abc123"));

        verify(urlService).getShortUrl(any(FullUrl.class));
    }

    @Test
    @DisplayName("Should return 400 for invalid request")
    void shouldReturnBadRequestForInvalidRequest() throws Exception {

        FullUrl request = new FullUrl();
        request.setFullUrl("");

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return all URLs")
    void shouldReturnAllUrls() throws Exception {

        UrlResponse first =
                new UrlResponse(
                        1L,
                        "https://google.com",
                        "abc123",
                        null
                );

        UrlResponse second =
                new UrlResponse(
                        2L,
                        "https://openai.com",
                        "xyz789",
                        "openai"
                );

        when(urlService.getAllUrls())
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/v1/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fullUrl")
                        .value("https://google.com"))
                .andExpect(jsonPath("$[0].shortUrl")
                        .value("abc123"))
                .andExpect(jsonPath("$[1].customAlias")
                        .value("openai"));

        verify(urlService).getAllUrls();
    }
    @Test
    @DisplayName("Should return analytics")
    void shouldReturnAnalytics() throws Exception {

        UrlAnalyticsResponse analytics =
                new UrlAnalyticsResponse(
                        1L,
                        "https://google.com",
                        "abc123",
                        null,
                        15L,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(urlService.getAnalytics(1L))
                .thenReturn(analytics);

        mockMvc.perform(get("/api/v1/urls/1/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullUrl")
                        .value("https://google.com"))
                .andExpect(jsonPath("$.shortUrl")
                        .value("abc123"))
                .andExpect(jsonPath("$.clickCount")
                        .value(15));

        verify(urlService).getAnalytics(1L);
    }

    @Test
    @DisplayName("Should redirect to original URL")
    void shouldRedirectToOriginalUrl() throws Exception {

        FullUrl fullUrl =
                new FullUrl("https://google.com");

        when(urlService.getFullUrl("abc123"))
                .thenReturn(fullUrl);

        mockMvc.perform(get("/api/v1/urls/abc123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://google.com"));

        verify(urlService).getFullUrl("abc123");
    }

    @Test
    @DisplayName("Should return 404 when short URL is not found")
    void shouldReturn404WhenShortUrlNotFound() throws Exception {

        when(urlService.getFullUrl("missing"))
                .thenThrow(new com.karthik.urlshortener.exception
                        .ShortUrlNotFoundException("missing"));

        mockMvc.perform(get("/api/v1/urls/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Short URL 'missing' not found."));

        verify(urlService).getFullUrl("missing");
    }

    @Test
    @DisplayName("Should return 410 when URL has expired")
    void shouldReturn410WhenUrlExpired() throws Exception {

        when(urlService.getFullUrl("expired"))
                .thenThrow(new com.karthik.urlshortener.exception
                        .UrlExpiredException("Short URL has expired."));

        mockMvc.perform(get("/api/v1/urls/expired"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value(410))
                .andExpect(jsonPath("$.error").value("Gone"))
                .andExpect(jsonPath("$.message")
                        .value("Short URL has expired."));

        verify(urlService).getFullUrl("expired");
    }
}