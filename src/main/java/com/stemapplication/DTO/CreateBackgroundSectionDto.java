package com.stemapplication.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBackgroundSectionDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Content cannot exceed 5000 characters")
    private String content;
    
    @Min(value = 1, message = "Display order must be at least 1")
    @Max(value = 9999, message = "Display order cannot exceed 9999")
    private Integer displayOrder = 999; // Default value
    
    @NotNull(message = "Is active status is required")
    private Boolean isActive = true; // Default value
}
