package com.stemapplication.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedAuthorDto {
    private Long id;
    private String name;
    private String initials;
    private String image; // URL to profile picture
}