package com.urlshortener.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO for incoming URL shortening requests.
 */
@Data
public class UrlRequest {

    @NotBlank(message = "URL is required")
    @Pattern(
        regexp = "^(https?://).+",
        message = "URL must start with http:// or https://"
    )
    private String originalUrl;

    // Optional: custom short alias (e.g., "my-blog")
    private String customAlias;

    // Optional: expiry in hours (0 = never expire)
    private Integer expiryHours;
}
