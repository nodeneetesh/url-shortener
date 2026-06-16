package com.urlshortener.repository;

import com.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.urlshortener.model.User;


/**
 * Repository interface for UrlMapping entity.
 * Spring Data JPA auto-implements all CRUD methods.
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    // Find by short code (used for redirect)
    Optional<UrlMapping> findByShortCode(String shortCode);

    // Find by custom alias
    Optional<UrlMapping> findByCustomAlias(String customAlias);

    // Check if short code already exists
    boolean existsByShortCode(String shortCode);

    // Check if custom alias already exists
    boolean existsByCustomAlias(String customAlias);

    // Get all active URLs
    List<UrlMapping> findByActiveTrue();

    List<UrlMapping> findByUser(User user);

    // Find by original URL (to avoid duplicate entries)
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);

    // Increment click count
    @Modifying
    @Query("UPDATE UrlMapping u SET u.clickCount = u.clickCount + 1 WHERE u.shortCode = :shortCode")
    void incrementClickCount(String shortCode);
}
