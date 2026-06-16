package com.urlshortener.controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/r")
@RequiredArgsConstructor
@Slf4j
public class RedirectController {

    private final UrlShortenerService service;

    // These paths should NOT be treated as short codes
    private static final Set<String> EXCLUDED = Set.of(
        "index.html", "favicon.ico", "h2-console",
        "api", "static", "error"
    );

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        // Skip if it's a static file or known path
        if (EXCLUDED.contains(shortCode) || shortCode.contains(".")) {
            return ResponseEntity.notFound().build();
        }

        log.info("Redirect request for: {}", shortCode);
        String originalUrl = service.resolveUrl(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }
}