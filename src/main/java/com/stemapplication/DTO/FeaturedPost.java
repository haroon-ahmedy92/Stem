package com.stemapplication.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedPost {
    private Long id; // Added ID for consistency
    private String title;
    private String excerpt;
    private FeaturedAuthorDto author; // Use FeaturedAuthorDto
    private String date;
    private String readTime;
}