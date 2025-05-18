package com.stemapplication.Controller;

import com.stemapplication.Models.*;
import com.stemapplication.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // CREATE
    @PostMapping("/posts")
    public ResponseEntity<BlogPost> createPost(@RequestBody BlogPost post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    // READ
    @GetMapping
    public ResponseEntity<BlogResponse> getBlogData() {
        return ResponseEntity.ok(postService.getBlogData());
    }

    @GetMapping("/posts")
    public ResponseEntity<List<BlogPost>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<BlogPost> getPostById(@PathVariable Long id) {
        BlogPost post = postService.getPostById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/posts/category/{categoryId}")
    public ResponseEntity<List<BlogPost>> getPostsByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId));
    }

    // UPDATE
    @PutMapping("/posts/{id}")
    public ResponseEntity<BlogPost> updatePost(
            @PathVariable Long id,
            @RequestBody BlogPost postDetails) {
        BlogPost updatedPost = postService.updatePost(id, postDetails);
        if (updatedPost != null) {
            return ResponseEntity.ok(updatedPost);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (postService.deletePost(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Additional endpoints
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(postService.getAllCategories());
    }

    @GetMapping("/featured")
    public ResponseEntity<FeaturedPost> getFeaturedPost() {
        FeaturedPost featuredPost = postService.getFeaturedPost();
        if (featuredPost != null) {
            return ResponseEntity.ok(featuredPost);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PopularArticle>> getPopularArticles() {
        return ResponseEntity.ok(postService.getPopularArticles());
    }
}