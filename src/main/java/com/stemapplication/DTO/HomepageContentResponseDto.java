package com.stemapplication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageContentResponseDto {
    
    private HeroSectionDto hero;
    
    private ActivitiesSectionDto activities;
    
    private OutcomesSectionDto outcomes;
    
    private SectionContentDto monitoring;
    
    private SectionContentDto ethics;
    
    private MetaDto meta;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetaDto {
        @JsonProperty("last_updated")
        private LocalDateTime lastUpdated;
        
        private String version = "1.0";
        
        @JsonProperty("sections_count")
        private Integer sectionsCount;
    }
}
