package com.stemapplication.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReorderBackgroundSectionsDto {
    
    @NotNull(message = "Sections order is required")
    @Size(min = 1, message = "At least one section ID must be provided")
    private List<Long> sectionsOrder;
}
