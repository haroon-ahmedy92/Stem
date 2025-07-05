package com.stemapplication.Service;

import com.stemapplication.DTO.*;

import java.util.List;
import java.util.Map;

public interface HomepageContentService {
    
    // Main content retrieval
    HomepageContentResponseDto getAllHomepageContent();
    HeroSectionDto getHeroContent();
    ActivitiesSectionDto getActivitiesContent(String featured, Integer limit, String color);
    OutcomesSectionDto getOutcomesContent();
    SectionContentDto getSectionContent(String section);
    
    // Hero management
    HeroSectionDto updateHeroContent(HeroSectionDto updateDto);
    
    // Activity management
    ActivityDto createActivity(CreateActivityDto createDto);
    ActivityDto updateActivity(Long id, UpdateActivityDto updateDto);
    void deleteActivity(Long id);
    List<ActivityDto> getAllActivities();
    
    // Outcome management
    OutcomeDto createOutcome(CreateOutcomeDto createDto);
    OutcomeDto updateOutcome(Long id, UpdateOutcomeDto updateDto);
    void deleteOutcome(Long id);
    List<OutcomeDto> getAllOutcomes();
    
    // Section content management
    SectionContentDto updateSectionContent(String section, UpdateSectionDto updateDto);
    
    // Search functionality
    Map<String, Object> searchContent(String query, String section, Integer limit);
    
    // Content reordering
    void reorderActivities(List<Long> itemIds);
    void reorderOutcomes(List<Long> itemIds);
    
    // Analytics
    Map<String, Object> getAnalytics();
}
