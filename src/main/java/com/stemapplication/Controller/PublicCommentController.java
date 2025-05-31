package com.stemapplication.Controller;

import com.stemapplication.Service.CommentService;
import com.stemapplication.DTO.CommentDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments") // Base path for public comments
public class PublicCommentController {

    private final CommentService commentService;

    @Autowired
    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Public endpoint: Get approved comments for a specific post.
     * No authentication required.
     *
     * @param blogPostId The ID of the blog post.
     * @return A list of approved comments for the specified post.
     */
    @GetMapping("/post/{blogPostId}")
    public ResponseEntity<List<CommentDto>> getApprovedCommentsForPost(@PathVariable Long blogPostId) {
        List<CommentDto> comments = commentService.getApprovedCommentsForPostDto(blogPostId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Endpoint for users (authenticated or guest) to post a new comment.
     * Authentication is optional: if a Principal is present, the comment is linked to a user;
     * otherwise, guest details are used.
     *
     * @param blogPostId The ID of the blog post to comment on.
     * @param payload A map containing "content", and optionally "guestAuthorName" and "guestAuthorEmail".
     * @param principal The authenticated user (if any).
     * @return ResponseEntity with the created comment DTO or an error message.
     */
    @PostMapping("/post/{blogPostId}")
    public ResponseEntity<?> createComment(
            @PathVariable Long blogPostId,
            @RequestBody Map<String, String> payload,
            Principal principal) {
        try {
            String commentContent = payload.get("content");
            if (commentContent == null || commentContent.trim().isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Comment content cannot be empty."), HttpStatus.BAD_REQUEST);
            }

            String username = (principal != null) ? principal.getName() : null;
            String guestAuthorName = payload.get("guestAuthorName");
            String guestAuthorEmail = payload.get("guestAuthorEmail");

            // Assuming commentService.createComment returns ResponseEntity<?> directly
            return commentService.createComment(blogPostId, commentContent, username, guestAuthorName, guestAuthorEmail);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Error posting comment: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}