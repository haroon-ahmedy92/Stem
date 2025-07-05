package com.stemapplication.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderRequestDto {
    
    @NotEmpty(message = "Item IDs list cannot be empty")
    @Valid
    private List<Long> itemIds;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOrder {
        private Long id;
        private Integer newOrder;
    }
}
