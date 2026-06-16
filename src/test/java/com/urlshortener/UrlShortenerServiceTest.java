package com.urlshortener;

import com.urlshortener.model.UrlMapping;
import com.urlshortener.model.UrlRequest;
import com.urlshortener.model.UrlResponse;
import com.urlshortener.repository.UrlMappingRepository;
import com.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @InjectMocks
    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(service, "shortCodeLength", 6);
    }

    @Test
    void testShortenUrl_Success() {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://www.google.com");

        UrlMapping saved = UrlMapping.builder()
                .id(1L)
                .originalUrl("https://www.google.com")
                .shortCode("abc123")
                .clickCount(0L)
                .active(true)
                .build();

        when(repository.existsByShortCode(anyString())).thenReturn(false);
        when(repository.save(any())).thenReturn(saved);

        UrlResponse response = service.shortenUrl(request);

        assertNotNull(response);
        assertEquals("https://www.google.com", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
        assertTrue(response.getShortUrl().contains("abc123"));
    }

    @Test
    void testShortenUrl_DuplicateCustomAlias() {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://www.google.com");
        request.setCustomAlias("mygoogle");

        when(repository.existsByCustomAlias("mygoogle")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.shortenUrl(request));
    }

    @Test
    void testResolveUrl_Success() {
        UrlMapping mapping = UrlMapping.builder()
                .id(1L)
                .originalUrl("https://www.google.com")
                .shortCode("abc123")
                .clickCount(0L)
                .active(true)
                .build();

        when(repository.findByCustomAlias("abc123")).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));
        doNothing().when(repository).incrementClickCount("abc123");

        String resolved = service.resolveUrl("abc123");

        assertEquals("https://www.google.com", resolved);
        verify(repository, times(1)).incrementClickCount("abc123");
    }

    @Test
    void testResolveUrl_NotFound() {
        when(repository.findByCustomAlias("xyz999")).thenReturn(Optional.empty());
        when(repository.findByShortCode("xyz999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.resolveUrl("xyz999"));
    }

    @Test
    void testResolveUrl_Deactivated() {
        UrlMapping mapping = UrlMapping.builder()
                .id(1L)
                .originalUrl("https://www.google.com")
                .shortCode("abc123")
                .active(false)
                .clickCount(0L)
                .build();

        when(repository.findByCustomAlias("abc123")).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.resolveUrl("abc123"));
        assertTrue(ex.getMessage().contains("deactivated"));
    }

    @Test
    void testResolveUrl_Expired() {
        UrlMapping mapping = UrlMapping.builder()
                .id(1L)
                .originalUrl("https://www.google.com")
                .shortCode("abc123")
                .active(true)
                .clickCount(0L)
                .expiresAt(LocalDateTime.now().minusHours(1)) // already expired
                .build();

        when(repository.findByCustomAlias("abc123")).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.resolveUrl("abc123"));
        assertTrue(ex.getMessage().contains("expired"));
    }

    @Test
    void testDeleteUrl_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.deleteUrl(1L));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUrl_NotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.deleteUrl(99L));
    }
}
