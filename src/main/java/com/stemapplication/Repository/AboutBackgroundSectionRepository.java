package com.stemapplication.Repository;

import com.stemapplication.Models.AboutBackgroundSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AboutBackgroundSectionRepository extends JpaRepository<AboutBackgroundSection, Long> {
    
    @Query("SELECT abs FROM AboutBackgroundSection abs WHERE abs.aboutBackground.id = :backgroundId ORDER BY abs.displayOrder ASC")
    List<AboutBackgroundSection> findByAboutBackgroundIdOrderByDisplayOrder(@Param("backgroundId") Long backgroundId);
    
    @Query("SELECT abs FROM AboutBackgroundSection abs WHERE abs.aboutBackground.id = :backgroundId AND abs.isActive = true ORDER BY abs.displayOrder ASC")
    List<AboutBackgroundSection> findActiveByAboutBackgroundIdOrderByDisplayOrder(@Param("backgroundId") Long backgroundId);
    
    @Modifying
    @Query("UPDATE AboutBackgroundSection abs SET abs.displayOrder = :order WHERE abs.id = :id")
    void updateDisplayOrder(@Param("id") Long id, @Param("order") Integer order);
    
    @Query("SELECT COALESCE(MAX(abs.displayOrder), 0) + 1 FROM AboutBackgroundSection abs WHERE abs.aboutBackground.id = :backgroundId")
    Integer getNextDisplayOrder(@Param("backgroundId") Long backgroundId);
}
