package com.stemapplication.Repository;

import com.stemapplication.Models.SpecificObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecificObjectiveRepository extends JpaRepository<SpecificObjective, Long> {
    
    @Query("SELECT so FROM SpecificObjective so WHERE so.aboutObjectives.id = :objectivesId AND so.isActive = true ORDER BY so.displayOrder ASC")
    List<SpecificObjective> findActiveByAboutObjectivesIdOrderByDisplayOrder(@Param("objectivesId") Long objectivesId);
    
    @Query("SELECT so FROM SpecificObjective so WHERE so.aboutObjectives.id = :objectivesId ORDER BY so.displayOrder ASC")
    List<SpecificObjective> findByAboutObjectivesIdOrderByDisplayOrder(@Param("objectivesId") Long objectivesId);
}
