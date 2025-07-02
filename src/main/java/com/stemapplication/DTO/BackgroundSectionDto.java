package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackgroundSectionDto {
    
    private Long id;
    private String title;
    private String content;
    private Integer displayOrder;
}
