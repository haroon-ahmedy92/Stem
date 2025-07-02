package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackgroundDto {
    
    private Long id;
    private String title;
    private String mainContent;
    private String ctaText;
    private String ctaLink;
    private List<BackgroundSectionDto> sections;
}
