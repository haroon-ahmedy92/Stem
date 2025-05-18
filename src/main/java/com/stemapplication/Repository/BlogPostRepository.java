package com.stemapplication.Repository;

import com.stemapplication.Models.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByCategoryId(String categoryId);
    List<BlogPost> findAllByOrderByCreatedAtDesc();
}