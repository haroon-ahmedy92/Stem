//package com.stemapplication.Controller;
//
//import com.stemapplication.Models.BlogPost;
//import com.stemapplication.Service.impl.AdminService;
//import com.stemapplication.Service.PostService;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/admin/posts")
//public class AdminPostController {
//
//    private final PostService postService;
//    private final AdminService adminService;
//
//    public AdminPostController(PostService postService, AdminService adminService) {
//        this.postService = postService;
//        this.adminService = adminService;
//    }
//
//    @PostMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public ResponseEntity<BlogPost> createPost(@Valid @RequestBody BlogPost post) {
//        if (!isAuthorizedAdmin()) {  // Fixed logic here
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(postService.createPost(post));
//        }
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public ResponseEntity<BlogPost> updatePost(@PathVariable Long id, @Valid @RequestBody BlogPost post) {
//        if (!isAuthorizedAdmin()) {  // Fixed logic here
//            Optional<BlogPost> updatedPost = Optional.ofNullable(postService.updatePost(id, post));
//            return updatedPost.map(ResponseEntity::ok)
//                    .orElse(ResponseEntity.notFound().build());
//        }
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
//        if (!isAuthorizedAdmin()) {  // Fixed logic here
//            boolean deleted = postService.deletePost(id);
//            return deleted ? ResponseEntity.noContent().build()
//                    : ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//    }
//
//    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public ResponseEntity<List<BlogPost>> getAllAdminPosts() {
//        return ResponseEntity.ok(postService.getAllPosts());
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public ResponseEntity<BlogPost> getAdminPostById(@PathVariable Long id) {
//        Optional<BlogPost> post = Optional.ofNullable(postService.getPostById(id));
//        return post.map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    private boolean isAuthorizedAdmin() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return auth != null && adminService.isAuthorizedToPost(auth.getName());
//    }
//}