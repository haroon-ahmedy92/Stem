package com.stemapplication.Repository;

import com.stemapplication.Models.HomepageActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomepageActivityRepository extends JpaRepository<HomepageActivity, Long> {
    
    /**
     * Find all published activities ordered by order index
     * @return List of published activities
     */
    @Query("SELECT a FROM HomepageActivity a WHERE a.isPublished = true ORDER BY a.orderIndex ASC, a.id ASC")
    List<HomepageActivity> findPublishedActivitiesOrdered();
    
    /**
     * Find featured activities that are published
     * @return List of featured published activities
     */
    @Query("SELECT a FROM HomepageActivity a WHERE a.isPublished = true AND a.isFeatured = true ORDER BY a.orderIndex ASC")
    List<HomepageActivity> findFeaturedPublishedActivities();
    
    /**
     * Find activities by color theme
     * @param color the color theme
     * @return List of activities with specified color
     */
    List<HomepageActivity> findByColorAndIsPublishedTrueOrderByOrderIndexAsc(String color);
    
    /**
     * Find activities with limit
     * @param limit maximum number of activities to return
     * @return Limited list of published activities
     */
    @Query(value = "SELECT a FROM HomepageActivity a WHERE a.isPublished = true ORDER BY a.orderIndex ASC, a.id ASC")
    List<HomepageActivity> findPublishedActivitiesWithLimit(@Param("limit") int limit);
    
    /**
     * Find activities containing search term in title or description
     * @param searchTerm the term to search for
     * @return List of matching activities
     */
    @Query("SELECT a FROM HomepageActivity a WHERE a.isPublished = true AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY a.orderIndex ASC")
    List<HomepageActivity> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    /**
     * Find the maximum order index
     * @return the highest order index
     */
    @Query("SELECT COALESCE(MAX(a.orderIndex), 0) FROM HomepageActivity a")
    Integer findMaxOrderIndex();
}
