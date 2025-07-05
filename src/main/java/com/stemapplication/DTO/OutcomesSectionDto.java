package com.stemapplication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutcomesSectionDto {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    @JsonProperty("background_color")
    private String backgroundColor;
    
    @JsonProperty("content_background")
    private String contentBackground;
    
    @JsonProperty("show_metrics")
    private Boolean showMetrics = true;
    
    private List<OutcomeDto> outcomes;
    
    @JsonProperty("overall_progress")
    private Double overallProgress;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
