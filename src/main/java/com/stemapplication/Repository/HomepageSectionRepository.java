package com.stemapplication.Repository;

import com.stemapplication.Models.HomepageSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomepageSectionRepository extends JpaRepository<HomepageSection, Long> {
    
    /**
     * Find section by section type
     * @param sectionType the type of section
     * @return Optional containing the section
     */
    Optional<HomepageSection> findBySectionType(HomepageSection.SectionType sectionType);
    
    /**
     * Find published section by section type
     * @param sectionType the type of section
     * @return Optional containing the published section
     */
    Optional<HomepageSection> findBySectionTypeAndIsPublishedTrue(HomepageSection.SectionType sectionType);
    
    /**
     * Check if section exists by type
     * @param sectionType the type of section
     * @return true if section exists
     */
    boolean existsBySectionType(HomepageSection.SectionType sectionType);
    
    /**
     * Find sections containing search term in title or description
     * @param searchTerm the term to search for
     * @return List of matching sections
     */
    @Query("SELECT s FROM HomepageSection s WHERE s.isPublished = true AND " +
           "(LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    java.util.List<HomepageSection> findBySearchTerm(@Param("searchTerm") String searchTerm);
}
