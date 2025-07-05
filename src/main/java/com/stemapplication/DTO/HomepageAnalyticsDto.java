package com.stemapplication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageAnalyticsDto {
    @JsonProperty("total_views")
    private Long totalViews;
    
    @JsonProperty("average_time_on_page")
    private Integer averageTimeOnPage;
    
    @JsonProperty("bounce_rate")
    private Double bounceRate;
    
    @JsonProperty("section_engagement")
    private Map<String, Object> sectionEngagement;
    
    @JsonProperty("device_breakdown")
    private Map<String, Double> deviceBreakdown;
    
    @JsonProperty("geographic_data")
    private Map<String, Object> geographicData;
    
    @JsonProperty("last_updated")
    private java.time.LocalDateTime lastUpdated;
}
