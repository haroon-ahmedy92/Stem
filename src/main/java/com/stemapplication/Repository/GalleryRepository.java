package com.stemapplication.Repository;

import com.stemapplication.Models.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    List<Gallery> findByCategory_Id(String categoryId);
    List<Gallery> findByCreatedBy_Id(Long userId);
    List<Gallery> findByTagsContaining(String tag);
    List<Gallery> findByFeaturedTrue();
}