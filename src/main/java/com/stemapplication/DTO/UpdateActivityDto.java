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
public class UpdateActivityDto {
    
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    private String description;
    
    @JsonProperty("icon_class")
    private String iconClass;
    
    private String color;
    
    private Integer order;
    
    @JsonProperty("is_featured")
    private Boolean isFeatured;
    
    @JsonProperty("is_published")
    private Boolean isPublished;
    
    @JsonProperty("additional_info")
    private String additionalInfo;
    
    private String link;
    
    private String image;
    
    private List<String> tags;
    
    private Map<String, Object> progress;
}
