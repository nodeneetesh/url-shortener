package com.urlshortener.controller;

import com.urlshortener.model.ApiResponse;
import com.urlshortener.model.UrlRequest;
import com.urlshortener.model.UrlResponse;
import com.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing all URL shortener APIs.
 *
 * Endpoints:
 * POST   /api/urls         → Shorten a URL
 * GET    /api/urls         → Get all URLs
 * GET    /api/urls/{id}    → Get URL by ID
 * GET    /api/urls/stats/{shortCode} → Get click stats
 * DELETE /api/urls/{id}    → Delete a URL
 * PATCH  /api/urls/{id}/deactivate  → Deactivate a URL
 */
@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")   // Allow frontend calls
public class UrlController {

    private final UrlShortenerService service;

    // ========================
    //   POST /api/urls
    //   Shorten a new URL
    // ========================
    @PostMapping
    public ResponseEntity<ApiResponse<UrlResponse>> shortenUrl(
            @Valid @RequestBody UrlRequest request) {

        log.info("API: Shorten URL request received");
        UrlResponse response = service.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("URL shortened successfully!", response));
    }

    // ========================
    //   GET /api/urls
    //   Get all URLs
    // ========================
    @GetMapping
    public ResponseEntity<ApiResponse<List<UrlResponse>>> getAllUrls() {
        List<UrlResponse> urls = service.getAllUrls();
        return ResponseEntity.ok(
                ApiResponse.success("Fetched " + urls.size() + " URLs", urls)
        );
    }

    // ========================
    //   GET /api/urls/{id}
    //   Get URL by ID
    // ========================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UrlResponse>> getUrlById(@PathVariable Long id) {
        UrlResponse url = service.getUrlById(id);
        return ResponseEntity.ok(ApiResponse.success("URL found", url));
    }

    // ========================
    //   GET /api/urls/stats/{shortCode}
    //   Get stats for a short code
    // ========================
    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<ApiResponse<UrlResponse>> getStats(@PathVariable String shortCode) {
        UrlResponse stats = service.getStats(shortCode);
        return ResponseEntity.ok(ApiResponse.success("Stats fetched", stats));
    }

    // ========================
    //   DELETE /api/urls/{id}
    //   Delete a URL mapping
    // ========================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUrl(@PathVariable Long id) {
        service.deleteUrl(id);
        return ResponseEntity.ok(ApiResponse.success("URL deleted successfully", null));
    }

    // ========================
    //   PATCH /api/urls/{id}/deactivate
    //   Deactivate a URL
    // ========================
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UrlResponse>> deactivateUrl(@PathVariable Long id) {
        UrlResponse response = service.deactivateUrl(id);
        return ResponseEntity.ok(ApiResponse.success("URL deactivated", response));
    }
}
