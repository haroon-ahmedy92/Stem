package com.stemapplication.Repository;

import com.stemapplication.Models.JustificationReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JustificationReferenceRepository extends JpaRepository<JustificationReference, Long> {
    
    @Query("SELECT jr FROM JustificationReference jr WHERE jr.aboutJustification.id = :justificationId ORDER BY jr.displayOrder ASC")
    List<JustificationReference> findByAboutJustificationIdOrderByDisplayOrder(@Param("justificationId") Long justificationId);
}
