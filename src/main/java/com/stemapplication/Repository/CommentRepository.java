package com.stemapplication.Repository;

import com.stemapplication.Models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // For public display
    List<Comment> findByBlogPostIdAndApprovedTrueOrderByCreatedAtDesc(Long blogPostId);

    // For moderation panel (unapproved comments)
    List<Comment> findByApprovedFalseOrderByCreatedAtDesc();

    // For admin to see all comments for a post (approved and unapproved)
    List<Comment> findByBlogPostIdOrderByCreatedAtDesc(Long blogPostId);
}

