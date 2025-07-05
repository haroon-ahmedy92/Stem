package com.stemapplication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitiesSectionDto {
    
    private Long id;
    
    private String title;
    
    private String subtitle;
    
    @JsonProperty("background_color")
    private String backgroundColor;
    
    private String layout = "grid";
    
    private Map<String, Integer> columns;
    
    private List<ActivityDto> activities;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
