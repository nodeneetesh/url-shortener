package com.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for URL Shortener Application
 *
 * Features:
 * - Shorten any long URL to a 6-char code
 * - Redirect using short code
 * - Track click count per URL
 * - View all URLs
 * - Delete a URL
 * - Custom alias support
 */
@SpringBootApplication
public class UrlShortenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortenerApplication.class, args);
    }
}
