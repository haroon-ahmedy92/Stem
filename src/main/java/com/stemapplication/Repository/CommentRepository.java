package com.stemapplication.Repository;

import com.stemapplication.Models.Comment;
import com.stemapplication.Models.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // For public view: find comments for a specific post with 'APPROVED' status
    List<Comment> findByPostIdAndStatusOrderByDateCreatedDesc(Long postId, CommentStatus status);

    // For admin view: find all comments with 'PENDING' status
    List<Comment> findByStatusOrderByDateCreatedDesc(CommentStatus status);

    // Find by ID and status (useful for admin updates)
    Optional<Comment> findByIdAndStatus(Long id, CommentStatus status);
}