package com.stemapplication.Repository;

import com.stemapplication.Models.HomepageOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomepageOutcomeRepository extends JpaRepository<HomepageOutcome, Long> {
    
    /**
     * Find all published outcomes ordered by order index
     * @return List of published outcomes
     */
    @Query("SELECT o FROM HomepageOutcome o WHERE o.isPublished = true ORDER BY o.orderIndex ASC, o.id ASC")
    List<HomepageOutcome> findPublishedOutcomesOrdered();
    
    /**
     * Find outcomes by status
     * @param status the outcome status
     * @return List of outcomes with specified status
     */
    List<HomepageOutcome> findByStatusAndIsPublishedTrueOrderByOrderIndexAsc(HomepageOutcome.OutcomeStatus status);
    
    /**
     * Find outcomes containing search term in title or description
     * @param searchTerm the term to search for
     * @return List of matching outcomes
     */
    @Query("SELECT o FROM HomepageOutcome o WHERE o.isPublished = true AND " +
           "(LOWER(o.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(o.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY o.orderIndex ASC")
    List<HomepageOutcome> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    /**
     * Find the maximum order index
     * @return the highest order index
     */
    @Query("SELECT COALESCE(MAX(o.orderIndex), 0) FROM HomepageOutcome o")
    Integer findMaxOrderIndex();
    
    /**
     * Count outcomes by status
     * @param status the outcome status
     * @return count of outcomes with specified status
     */
    long countByStatusAndIsPublishedTrue(HomepageOutcome.OutcomeStatus status);
}
