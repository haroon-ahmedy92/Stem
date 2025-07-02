package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecificObjectiveDto {
    
    private Long id;
    private String title;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
}
