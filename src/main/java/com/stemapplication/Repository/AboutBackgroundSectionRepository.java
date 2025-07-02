package com.stemapplication.Repository;

import com.stemapplication.Models.AboutBackgroundSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AboutBackgroundSectionRepository extends JpaRepository<AboutBackgroundSection, Long> {
    
    @Query("SELECT abs FROM AboutBackgroundSection abs WHERE abs.aboutBackground.id = :backgroundId ORDER BY abs.displayOrder ASC")
    List<AboutBackgroundSection> findByAboutBackgroundIdOrderByDisplayOrder(@Param("backgroundId") Long backgroundId);
}
