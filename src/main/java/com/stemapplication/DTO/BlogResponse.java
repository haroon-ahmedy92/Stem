package com.stemapplication.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponse {
    private BlogData blogData; // BlogData itself can remain a simple POJO or become a DTO
    private List<CategoryDto> categories; // Use CategoryDto
    private List<BlogPostDto> blogPosts; // Use BlogPostDto
    private List<PopularArticle> popularArticles; // This can remain as is if it's already a simple POJO
}