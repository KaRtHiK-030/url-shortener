package com.karthik.urlshortener.controller;

import com.karthik.urlshortener.entity.UrlEntity;
import com.karthik.urlshortener.exception.ShortUrlNotFoundException;
import com.karthik.urlshortener.repository.UrlRepository;
import com.karthik.urlshortener.service.QrCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/qr")
public class QrCodeController {

    private final QrCodeService qrCodeService;
    private final UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public QrCodeController(QrCodeService qrCodeService,
                            UrlRepository urlRepository) {
        this.qrCodeService = qrCodeService;
        this.urlRepository = urlRepository;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable String shortUrl) {

        UrlEntity urlEntity = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));

        String shortLink = baseUrl + "/" + urlEntity.getShortUrl();

        byte[] qrCode = qrCodeService.generateQRCode(shortLink, 300, 300);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"qr.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }
}