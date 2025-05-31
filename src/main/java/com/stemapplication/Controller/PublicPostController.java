package com.stemapplication.Controller;

import com.stemapplication.Service.PostService;
import com.stemapplication.DTO.BlogPostDto;
import com.stemapplication.DTO.CategoryDto;
import com.stemapplication.DTO.BlogResponse;
import com.stemapplication.DTO.FeaturedPost;
import com.stemapplication.DTO.PopularArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog") // Base path for public blog operations
public class PublicPostController {

    private final PostService postService;

    @Autowired
    public PublicPostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Public endpoint: Get overall blog data (e.g., summary, statistics).
     * No authentication required.
     *
     * @return A BlogResponse DTO containing various blog data.
     */
    @GetMapping
    public ResponseEntity<BlogResponse> getBlogData() {
        return ResponseEntity.ok(postService.getBlogData());
    }

    /**
     * Public endpoint: Get a list of all blog posts.
     * No authentication required.
     *
     * @return A list of BlogPostDto objects.
     */
    @GetMapping("/posts")
    public ResponseEntity<List<BlogPostDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPostsDto());
    }

    /**
     * Public endpoint: Get a specific blog post by its ID.
     * No authentication required.
     *
     * @param id The ID of the blog post.
     * @return The BlogPostDto if found, otherwise 404 Not Found.
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<BlogPostDto> getPostById(@PathVariable Long id) {
        BlogPostDto post = postService.getPostByIdDto(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Public endpoint: Get blog posts filtered by category.
     * No authentication required.
     *
     * @param categoryId The ID of the category.
     * @return A list of BlogPostDto objects belonging to the specified category.
     */
    @GetMapping("/posts/category/{categoryId}")
    public ResponseEntity<List<BlogPostDto>> getPostsByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(postService.getPostsByCategoryDto(categoryId));
    }

    /**
     * Public endpoint: Get a list of all available blog categories.
     * No authentication required.
     *
     * @return A list of CategoryDto objects.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(postService.getAllCategoriesDto());
    }

    /**
     * Public endpoint: Get the featured blog post.
     * No authentication required.
     *
     * @return The FeaturedPost DTO if found, otherwise 404 Not Found.
     */
    @GetMapping("/featured")
    public ResponseEntity<FeaturedPost> getFeaturedPost() {
        FeaturedPost featuredPost = postService.getFeaturedPost();
        if (featuredPost != null) {
            return ResponseEntity.ok(featuredPost);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Public endpoint: Get a list of popular articles.
     * No authentication required.
     *
     * @return A list of PopularArticle objects.
     */
    @GetMapping("/popular")
    public ResponseEntity<List<PopularArticle>> getPopularArticles() {
        return ResponseEntity.ok(postService.getPopularArticles());
    }
}