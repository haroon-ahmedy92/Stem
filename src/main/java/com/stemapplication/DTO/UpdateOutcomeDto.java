package com.stemapplication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOutcomeDto {
    
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    private String description;
    
    @JsonProperty("icon_class")
    private String iconClass;
    
    private Integer order;
    
    @JsonProperty("is_published")
    private Boolean isPublished;
    
    private Map<String, Object> metrics;
    
    private String status;
    
    private List<Map<String, Object>> milestones;
}
