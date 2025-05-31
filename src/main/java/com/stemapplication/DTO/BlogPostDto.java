package com.stemapplication.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List; // If you decide to include CommentDto later

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostDto {
    private Long id;
    private String image;
    private String alt;
    private String title;
    private String content;
    private UserDto author;
    private CategoryDto category;
    private ReactionsDto reactions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String documentUrl;
    // private List<CommentDto> commentsList; // Keep commented out for now for simplicity and performance
}