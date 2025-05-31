package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogData {
    private String title;
    private String description;
    private FeaturedPost featuredPost;

}