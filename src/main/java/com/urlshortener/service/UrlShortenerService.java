package com.urlshortener.service;

import com.urlshortener.model.*;
import com.urlshortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Core business logic for URL shortening.
 *
 * Responsibilities:
 * 1. Generate unique short codes
 * 2. Save URL mappings to DB
 * 3. Resolve short code → original URL
 * 4. Track click count
 * 5. CRUD operations on URLs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UrlShortenerService {

    private final UrlMappingRepository repository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.short-code-length:6}")
    private int shortCodeLength;

    // Characters used for generating short codes
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

    // ========================
    //   SHORTEN URL
    // ========================
    @Transactional
    public UrlResponse shortenUrl(UrlRequest request) {
        log.info("Shortening URL: {}", request.getOriginalUrl());

        // Validate custom alias if provided
        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            if (repository.existsByCustomAlias(request.getCustomAlias())) {
                throw new IllegalArgumentException("Custom alias '" + request.getCustomAlias() + "' is already taken!");
            }
            if (!request.getCustomAlias().matches("^[a-zA-Z0-9_-]+$")) {
                throw new IllegalArgumentException("Custom alias can only contain letters, numbers, hyphens, and underscores.");
            }
        }

        // Generate unique short code
        String shortCode = generateUniqueShortCode();

        // Calculate expiry
        LocalDateTime expiresAt = null;
        if (request.getExpiryHours() != null && request.getExpiryHours() > 0) {
            expiresAt = LocalDateTime.now().plusHours(request.getExpiryHours());
        }

        // Build and save entity
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .customAlias(request.getCustomAlias())
                .expiresAt(expiresAt)
                .build();

        UrlMapping saved = repository.save(mapping);
        log.info("URL saved with short code: {}", shortCode);

        return toResponse(saved);
    }

    // ========================
    //   RESOLVE SHORT CODE
    // ========================
    @Transactional
    public String resolveUrl(String shortCode) {
        log.info("Resolving short code: {}", shortCode);

        // First try custom alias
        UrlMapping mapping = repository.findByCustomAlias(shortCode)
                .orElseGet(() -> repository.findByShortCode(shortCode)
                        .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode)));

        // Check if active
        if (!mapping.getActive()) {
            throw new RuntimeException("This short URL has been deactivated.");
        }

        // Check if expired
        if (mapping.isExpired()) {
            throw new RuntimeException("This short URL has expired.");
        }

        // Increment click count
        repository.incrementClickCount(mapping.getShortCode());
        log.info("Redirecting to: {}", mapping.getOriginalUrl());

        return mapping.getOriginalUrl();
    }

    // ========================
    //   GET ALL URLS
    // ========================
    @Transactional(readOnly = true)
    public List<UrlResponse> getAllUrls() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========================
    //   GET URL BY ID
    // ========================
    @Transactional(readOnly = true)
    public UrlResponse getUrlById(Long id) {
        UrlMapping mapping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found with id: " + id));
        return toResponse(mapping);
    }

    // ========================
    //   GET STATS BY SHORT CODE
    // ========================
    @Transactional(readOnly = true)
    public UrlResponse getStats(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode));
        return toResponse(mapping);
    }

    // ========================
    //   DELETE URL
    // ========================
    @Transactional
    public void deleteUrl(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("URL not found with id: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted URL with id: {}", id);
    }

    // ========================
    //   DEACTIVATE URL
    // ========================
    @Transactional
    public UrlResponse deactivateUrl(Long id) {
        UrlMapping mapping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found with id: " + id));
        mapping.setActive(false);
        return toResponse(repository.save(mapping));
    }

    // ========================
    //   PRIVATE HELPERS
    // ========================

    private String generateUniqueShortCode() {
        String code;
        int attempts = 0;
        do {
            code = generateShortCode();
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Failed to generate unique short code after 10 attempts");
            }
        } while (repository.existsByShortCode(code));
        return code;
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(shortCodeLength);
        for (int i = 0; i < shortCodeLength; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private UrlResponse toResponse(UrlMapping mapping) {
        // Use custom alias in short URL if available, otherwise use short code
        String codeOrAlias = (mapping.getCustomAlias() != null && !mapping.getCustomAlias().isBlank())
                ? mapping.getCustomAlias()
                : mapping.getShortCode();

        return UrlResponse.builder()
                .id(mapping.getId())
                .originalUrl(mapping.getOriginalUrl())
                .shortCode(mapping.getShortCode())
                .shortUrl(baseUrl + "/r/" + codeOrAlias)
                .customAlias(mapping.getCustomAlias())
                .clickCount(mapping.getClickCount())
                .createdAt(mapping.getCreatedAt())
                .expiresAt(mapping.getExpiresAt())
                .active(mapping.getActive())
                .build();
    }
}
