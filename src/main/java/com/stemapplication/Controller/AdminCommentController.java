package com.stemapplication.Controller;

import com.stemapplication.Service.CommentService;
import com.stemapplication.DTO.CommentDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/comments") // Base path for admin-level comment operations
public class AdminCommentController {

    private final CommentService commentService;

    @Autowired
    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Admin/SuperAdmin endpoint: Get all unapproved comments for moderation.
     * Requires ROLE_ADMIN or ROLE_SUPER_ADMIN.
     *
     * @return A list of unapproved comments.
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<CommentDto>> getUnapprovedComments() {
        List<CommentDto> comments = commentService.getUnapprovedCommentsDto();
        return ResponseEntity.ok(comments);
    }

    /**
     * Admin/SuperAdmin endpoint: Approve a specific comment.
     * Requires ROLE_ADMIN or ROLE_SUPER_ADMIN.
     *
     * @param commentId The ID of the comment to approve.
     * @param principal The authenticated user (admin/superadmin) performing the approval.
     * @return ResponseEntity with a success message or an error message.
     */
    @PutMapping("/approve/{commentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> approveComment(@PathVariable Long commentId, Principal principal) {
        try {
            String approverUsername = principal.getName();
            return commentService.approveComment(commentId, approverUsername);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Error approving comment: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Admin/SuperAdmin endpoint: Delete a specific comment (reject).
     * Requires ROLE_ADMIN or ROLE_SUPER_ADMIN.
     *
     * @param commentId The ID of the comment to delete.
     * @return ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("/delete/{commentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            return commentService.deleteComment(commentId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Error deleting comment: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Admin/SuperAdmin endpoint: Get all comments for a specific post (including unapproved ones).
     * This is for moderation purposes.
     * Requires ROLE_ADMIN or ROLE_SUPER_ADMIN.
     *
     * @param blogPostId The ID of the blog post.
     * @return A list of all comments (approved and unapproved) for the specified post.
     */
    @GetMapping("/post/{blogPostId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    // FIX: Changed return type to ResponseEntity<?> to allow different return types in catch blocks
    public ResponseEntity<?> getAllCommentsForPostAdmin(@PathVariable Long blogPostId) {
        try {
            List<CommentDto> comments = commentService.getAllCommentsForPostAdminDto(blogPostId);
            return ResponseEntity.ok(comments);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Error retrieving comments for post: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}