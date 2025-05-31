package com.stemapplication.Controller;

import com.stemapplication.Models.BlogPost; 
import com.stemapplication.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.stemapplication.DTO.BlogPostDto; 

@RestController
@RequestMapping("/api/blog/posts") // Base path for authenticated blog post operations
public class AuthenticatedPostController {

    private final PostService postService;

    @Autowired
    public AuthenticatedPostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Authenticated endpoint: Create a new blog post.
     * Requires ROLE_USER, ROLE_ADMIN, or ROLE_SUPER_ADMIN.
     *
     * @param post The BlogPost entity to create.
     * @param principal The authenticated user creating the post.
     * @return ResponseEntity with the created BlogPost entity or an error message.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> createPost(@RequestBody BlogPost post, Principal principal) {
        try {
            String username = principal.getName();
            postService.createPost(post, username);
            return new ResponseEntity<>(Map.of("message", "Blog post created successfully!"), HttpStatus.CREATED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error creating post: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authenticated endpoint: Update an existing blog post.
     * Requires ROLE_USER (for own posts), ROLE_ADMIN, or ROLE_SUPER_ADMIN (for any post).
     *
     * @param id The ID of the post to update.
     * @param postDetails The updated BlogPost entity.
     * @param principal The authenticated user updating the post.
     * @return ResponseEntity with the updated BlogPostDto or an error message.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody BlogPost postDetails,
            Principal principal) {
        try {
            String username = principal.getName();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<String> userRoles = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList());

            BlogPostDto updatedPostDto = postService.updatePost(id, postDetails, username, userRoles);
            return ResponseEntity.ok(updatedPostDto);
        } catch (SecurityException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error updating post: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authenticated endpoint: Delete a blog post.
     * Requires ROLE_USER (for own posts), ROLE_ADMIN, or ROLE_SUPER_ADMIN (for any post).
     *
     * @param id The ID of the post to delete.
     * @param principal The authenticated user deleting the post.
     * @return ResponseEntity indicating success (204 No Content) or an error message.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Principal principal) {
        try {
            String username = principal.getName();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<String> userRoles = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList());

            if (postService.deletePost(id, username, userRoles)) {
                return ResponseEntity.noContent().build();
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // If deletePost returns false for some reason (e.g., not found before actual check)
        } catch (SecurityException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Error deleting post: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}