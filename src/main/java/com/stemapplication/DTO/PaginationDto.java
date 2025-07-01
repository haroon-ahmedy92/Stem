package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDto {
    
    private long total;
    private int limit;
    private int offset;
    private boolean hasNext;
    private boolean hasPrevious;
}
