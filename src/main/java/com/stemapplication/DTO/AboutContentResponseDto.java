package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AboutContentResponseDto {
    
    private BackgroundDto background;
    private List<BenefitDto> benefits;
    private JustificationDto justification;
    private ObjectivesDto objectives;
    private LocalDateTime lastUpdated;
}
