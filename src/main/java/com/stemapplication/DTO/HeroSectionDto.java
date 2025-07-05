package com.stemapplication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeroSectionDto {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title cannot exceed 500 characters")
    private String title;
    
    private String subtitle;
    
    @JsonProperty("background_image")
    private String backgroundImage;
    
    @JsonProperty("background_gradient")
    private String backgroundGradient;
    
    @JsonProperty("cta_text")
    private String ctaText;
    
    @JsonProperty("cta_link")
    private String ctaLink;
    
    @JsonProperty("cta_color")
    private String ctaColor;
    
    @JsonProperty("search_placeholder")
    private String searchPlaceholder;
    
    @JsonProperty("search_enabled")
    private Boolean searchEnabled;
    
    private Map<String, Object> animations;
    
    @JsonProperty("responsive_settings")
    private Map<String, Object> responsiveSettings;
    
    @JsonProperty("is_published")
    private Boolean isPublished;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
