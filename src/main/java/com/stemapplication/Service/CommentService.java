package com.stemapplication.Service;

import com.stemapplication.Models.*; // Still need actual Entities
import com.stemapplication.Repository.BlogPostRepository;
import com.stemapplication.Repository.CommentRepository;
import com.stemapplication.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors; // For stream().map().collect()

// Import DTOs for comments
import com.stemapplication.DTO.CommentDto;
import com.stemapplication.DTO  .UserDto;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final PostService postService;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          BlogPostRepository blogPostRepository,
                          UserRepository userRepository,
                          PostService postService) {
        this.commentRepository = commentRepository;
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;
        this.postService = postService;
    }

    // User/Guest creates a comment (initially unapproved) - returns String message for now
    @Transactional
    public ResponseEntity<Map<String, String>> createComment(Long blogPostId, String commentContent,
                                                             String username, String guestAuthorName, String guestAuthorEmail) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(blogPostId);
        if (blogPostOptional.isEmpty()) {
            throw new EntityNotFoundException("Blog post not found with ID: " + blogPostId);
        }
        BlogPost blogPost = blogPostOptional.get();

        Comment comment = new Comment();
        comment.setContent(commentContent);
        comment.setBlogPost(blogPost);
        comment.setApproved(false);

        if (username != null && !username.isEmpty()) {
            // If you added `private UserEntity commenter;` to your Comment model, you'd set it here.
            // UserEntity user = userRepository.findByUsername(username)
            //         .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            // comment.setCommenter(user);
            // Since Comment doesn't have a direct `commenter` field, we don't set it here explicitly.
            // The fact that `username` is present indicates it was an authenticated user.
        } else {
            if (guestAuthorName == null || guestAuthorName.trim().isEmpty() ||
                    guestAuthorEmail == null || guestAuthorEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("Guest comments require both name and email.");
            }
            comment.setGuestAuthorName(guestAuthorName);
            comment.setGuestAuthorEmail(guestAuthorEmail);
        }

        commentRepository.save(comment);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment submitted successfully and is awaiting moderation.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all approved comments for a specific blog post (publicly visible) - returns DTOs
    public List<CommentDto> getApprovedCommentsForPostDto(Long blogPostId) {
        return commentRepository.findByBlogPostIdAndApprovedTrueOrderByCreatedAtDesc(blogPostId).stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }

    // Get all unapproved comments (for admin/moderator dashboard) - returns DTOs
    public List<CommentDto> getUnapprovedCommentsDto() {
        return commentRepository.findByApprovedFalseOrderByCreatedAtDesc().stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }

    // Admin/Moderator approves a comment - returns String message
    @Transactional
    public ResponseEntity<Map<String, String>> approveComment(Long commentId, String approverUsername) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            throw new EntityNotFoundException("Comment not found with ID: " + commentId);
        }
        Comment comment = commentOptional.get();

        if (comment.isApproved()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment is already approved.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        UserEntity approver = userRepository.findByUsername(approverUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Approver user not found with username: " + approverUsername));

        comment.setApproved(true);
        comment.setApprovedAt(LocalDateTime.now());
        comment.setApprovedBy(approver);
        commentRepository.save(comment);

        postService.incrementCommentCount(comment.getBlogPost().getId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment approved successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Admin/Moderator rejects/deletes a comment - returns String message
    @Transactional
    public ResponseEntity<Map<String, String>> deleteComment(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            throw new EntityNotFoundException("Comment not found with ID: " + commentId);
        }
        Comment comment = commentOptional.get();
        boolean wasApproved = comment.isApproved();

        commentRepository.delete(comment);

        if (wasApproved) {
            postService.decrementCommentCount(comment.getBlogPost().getId());
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Comment deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get all comments for a specific blog post (including unapproved, for admin) - returns DTOs
    public List<CommentDto> getAllCommentsForPostAdminDto(Long blogPostId) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(blogPostId);
        if (blogPostOptional.isEmpty()) {
            throw new EntityNotFoundException("Blog post not found with ID: " + blogPostId);
        }
        return commentRepository.findByBlogPostIdOrderByCreatedAtDesc(blogPostId).stream()
                .map(this::convertToCommentDto)
                .collect(Collectors.toList());
    }

    // MAPPING METHOD for Comment to CommentDto
    private CommentDto convertToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setApproved(comment.isApproved());
        dto.setGuestAuthorName(comment.getGuestAuthorName());

        if (comment.getApprovedBy() != null) {
            dto.setApprovedBy(convertToUserDtoForComment(comment.getApprovedBy()));
        }
        dto.setApprovedAt(comment.getApprovedAt()); // Will be null if not approved

        return dto;
    }

    // A slightly different UserDto conversion for comments, e.g., if you only need fewer details
    private UserDto convertToUserDtoForComment(UserEntity userEntity) {
        UserDto dto = new UserDto();
        dto.setId(userEntity.getId());
        dto.setUsername(userEntity.getUsername());
        dto.setName(userEntity.getName());
        // You might or might not include email or profile picture for an approver.
        // dto.setEmail(userEntity.getEmail());
        // dto.setProfilePictureUrl(userEntity.getProfilePictureUrl());
        return dto;
    }
}