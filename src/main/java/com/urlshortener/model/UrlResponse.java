package com.urlshortener.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO returned to the client after shortening a URL.
 */
@Data
@Builder
public class UrlResponse {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private String shortUrl;         // Full short URL e.g. http://localhost:8080/abc123
    private String customAlias;
    private Long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean active;
}
