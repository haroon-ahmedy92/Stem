package com.stemapplication.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBackgroundSectionDto {
    
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    @Size(max = 5000, message = "Content cannot exceed 5000 characters")
    private String content;
    
    @Min(value = 1, message = "Display order must be at least 1")
    @Max(value = 9999, message = "Display order cannot exceed 9999")
    private Integer displayOrder;
    
    private Boolean isActive;
}
