package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceDto {
    
    private Long id;
    private String title;
    private String url;
    private String author;
    private String publicationDate;
    private Integer displayOrder;
}
