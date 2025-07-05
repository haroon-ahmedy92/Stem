package com.stemapplication.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.stemapplication.Utils.JsonConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "homepage_hero")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageHero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title cannot exceed 500 characters")
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    
    @Column(name = "subtitle", columnDefinition = "TEXT")
    private String subtitle;
    
    @Column(name = "background_image", length = 500)
    private String backgroundImage;
    
    @Column(name = "background_gradient", length = 200)
    private String backgroundGradient;
    
    @Size(max = 50, message = "CTA text cannot exceed 50 characters")
    @Column(name = "cta_text", length = 50)
    private String ctaText;
    
    @Column(name = "cta_link", length = 500)
    private String ctaLink;
    
    @Column(name = "cta_color", length = 7)
    private String ctaColor;
    
    @Size(max = 100, message = "Search placeholder cannot exceed 100 characters")
    @Column(name = "search_placeholder", length = 100)
    private String searchPlaceholder = "Search for programs...";
    
    @Column(name = "search_enabled")
    private Boolean searchEnabled = true;
    
    @Convert(converter = JsonConverter.class)
    @Column(name = "animations", columnDefinition = "TEXT")
    private Map<String, Object> animations;
    
    @Convert(converter = JsonConverter.class)
    @Column(name = "responsive_settings", columnDefinition = "TEXT")
    private Map<String, Object> responsiveSettings;
    
    @Column(name = "is_published")
    private Boolean isPublished = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
