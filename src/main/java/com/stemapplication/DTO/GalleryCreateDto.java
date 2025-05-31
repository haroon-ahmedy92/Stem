package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryCreateDto {
    private String title;
    private String description;
    private String category;
    private List<Map<String, String>> tags;
    private String image;
    private boolean featured;
}