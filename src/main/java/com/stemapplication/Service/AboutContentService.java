package com.stemapplication.Service;

import com.stemapplication.DTO.*;

import java.util.List;

public interface AboutContentService {
    
    /**
     * Get all about page content
     */
    AboutContentResponseDto getAllAboutContent();
    
    /**
     * Get specific section content
     */
    Object getSectionContent(String section);
    
    /**
     * Update section content (Admin only)
     */
    Object updateSectionContent(String section, Object updateDto);
    
    /**
     * Get all benefits
     */
    List<BenefitDto> getAllBenefits();
    
    /**
     * Create new benefit (Admin only)
     */
    BenefitDto createBenefit(CreateBenefitDto createDto);
    
    /**
     * Update benefit (Admin only)
     */
    BenefitDto updateBenefit(Long id, UpdateBenefitDto updateDto);
    
    /**
     * Delete benefit (Admin only)
     */
    void deleteBenefit(Long id);
    
    /**
     * Get all specific objectives
     */
    List<SpecificObjectiveDto> getAllSpecificObjectives();
    
    /**
     * Create new specific objective (Admin only)
     */
    SpecificObjectiveDto createSpecificObjective(CreateObjectiveDto createDto);
    
    /**
     * Create new background section (Admin only)
     */
    BackgroundSectionDto createBackgroundSection(CreateBackgroundSectionDto createDto);
    
    /**
     * Update background section (Admin only)
     */
    BackgroundSectionDto updateBackgroundSection(Long id, UpdateBackgroundSectionDto updateDto);
    
    /**
     * Delete background section (Admin only)
     */
    void deleteBackgroundSection(Long id);
    
    /**
     * Reorder background sections (Admin only)
     */
    List<BackgroundSectionDto> reorderBackgroundSections(ReorderBackgroundSectionsDto reorderDto);
    
    /**
     * Get analytics data (Admin only)
     */
    Object getAnalytics();
}
