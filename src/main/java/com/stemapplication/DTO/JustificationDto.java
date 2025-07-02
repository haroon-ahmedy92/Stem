package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JustificationDto {
    
    private Long id;
    private String title;
    private String content;
    private String conclusion;
    private List<ReferenceDto> references;
}
