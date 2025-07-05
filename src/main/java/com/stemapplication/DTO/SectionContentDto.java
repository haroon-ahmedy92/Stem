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
public class SectionContentDto {
    
    private Long id;
    
    @JsonProperty("section_type")
    private String sectionType;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    private String subtitle;
    
    private String description;
    
    private String content;
    
    @JsonProperty("background_color")
    private String backgroundColor;
    
    @JsonProperty("content_background")
    private String contentBackground;
    
    @JsonProperty("additional_data")
    private Map<String, Object> additionalData;
    
    @JsonProperty("is_published")
    private Boolean isPublished;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
