package com.stemapplication.DTO;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSectionDto {
    
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    private String content;
    
    @Size(max = 255, message = "Button text cannot exceed 255 characters")
    private String buttonText;
    
    @Size(max = 500, message = "Button link cannot exceed 500 characters")
    private String buttonLink;
    
    @Size(max = 7, message = "Background color must be a valid hex color")
    private String backgroundColor;
    
    @Size(max = 7, message = "Text color must be a valid hex color")
    private String textColor;
    
    private Boolean isActive;
    
    private Integer displayOrder;
}
