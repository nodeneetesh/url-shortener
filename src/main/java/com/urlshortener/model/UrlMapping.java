package com.urlshortener.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a URL mapping in the database.
 * Maps a short code to an original long URL.
 */
@Entity
@Table(name = "url_mappings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The original long URL submitted by user
    @Column(name = "original_url", nullable = false, length = 2048)
    @NotBlank(message = "Original URL cannot be blank")
    private String originalUrl;

    // The unique short code (e.g., "abc123")
    @Column(name = "short_code", nullable = false, unique = true, length = 20)
    private String shortCode;

    // Optional custom alias set by user
    @Column(name = "custom_alias", unique = true, length = 50)
    private String customAlias;

    // Number of times this short URL was accessed
    @Column(name = "click_count", nullable = false)
    @Builder.Default
    private Long clickCount = 0L;

    // When this mapping was created
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Expiry date (null = never expires)
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Active/inactive flag
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Helper method to check if URL is expired
    public boolean isExpired() {
        if (expiresAt == null) return false;
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
