package com.stemapplication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutcomeDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @JsonProperty("icon_class")
    private String iconClass;
    
    private Integer order;
    
    private Map<String, Object> metrics;
    
    private String status = "on-track";
    
    private List<Map<String, Object>> milestones;
}
