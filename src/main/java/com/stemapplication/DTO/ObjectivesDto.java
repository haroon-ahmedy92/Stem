package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectivesDto {
    
    private Long id;
    private String title;
    private String introduction;
    private String conclusion;
    private List<SpecificObjectiveDto> specificObjectives;
}
