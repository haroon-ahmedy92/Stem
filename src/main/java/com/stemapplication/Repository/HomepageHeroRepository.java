package com.stemapplication.Repository;

import com.stemapplication.Models.HomepageHero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomepageHeroRepository extends JpaRepository<HomepageHero, Long> {
    
    /**
     * Find the published hero content
     * @return Optional containing the first published hero section
     */
    @Query("SELECT h FROM HomepageHero h WHERE h.isPublished = true ORDER BY h.updatedAt DESC")
    Optional<HomepageHero> findPublishedHero();
    
    /**
     * Find the latest hero content regardless of publish status
     * @return Optional containing the latest hero section
     */
    @Query("SELECT h FROM HomepageHero h ORDER BY h.updatedAt DESC")
    Optional<HomepageHero> findLatestHero();
    
    /**
     * Check if any published hero content exists
     * @return true if published hero exists
     */
    boolean existsByIsPublishedTrue();
}
