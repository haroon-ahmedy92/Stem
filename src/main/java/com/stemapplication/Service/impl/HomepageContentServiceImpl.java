package com.stemapplication.Service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stemapplication.DTO.*;
import com.stemapplication.Models.*;
import com.stemapplication.Repository.*;
import com.stemapplication.Service.HomepageContentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class HomepageContentServiceImpl implements HomepageContentService {
    
    private final HomepageHeroRepository heroRepository;
    private final HomepageActivityRepository activityRepository;
    private final HomepageOutcomeRepository outcomeRepository;
    private final HomepageSectionRepository sectionRepository;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public HomepageContentServiceImpl(
            HomepageHeroRepository heroRepository,
            HomepageActivityRepository activityRepository,
            HomepageOutcomeRepository outcomeRepository,
            HomepageSectionRepository sectionRepository,
            ObjectMapper objectMapper) {
        this.heroRepository = heroRepository;
        this.activityRepository = activityRepository;
        this.outcomeRepository = outcomeRepository;
        this.sectionRepository = sectionRepository;
        this.objectMapper = objectMapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public HomepageContentResponseDto getAllHomepageContent() {
        log.debug("Getting all homepage content");
        
        HomepageContentResponseDto response = new HomepageContentResponseDto();
        
        // Get hero content
        response.setHero(getHeroContent());
        
        // Get activities section
        response.setActivities(getActivitiesContent(null, null, null));
        
        // Get outcomes section
        response.setOutcomes(getOutcomesContent());
        
        // Get monitoring section
        response.setMonitoring(getSectionContent("monitoring"));
        
        // Get ethics section
        response.setEthics(getSectionContent("ethics"));
        
        // Set metadata
        HomepageContentResponseDto.MetaDto meta = new HomepageContentResponseDto.MetaDto();
        meta.setLastUpdated(LocalDateTime.now());
        meta.setVersion("1.0");
        meta.setSectionsCount(5);
        response.setMeta(meta);
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public HeroSectionDto getHeroContent() {
        log.debug("Getting hero content");
        
        HomepageHero hero = heroRepository.findPublishedHero()
                .orElse(null);
        
        if (hero == null) {
            return null;
        }
        
        return convertHeroToDto(hero);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ActivitiesSectionDto getActivitiesContent(String featured, Integer limit, String color) {
        log.debug("Getting activities content with filters - featured: {}, limit: {}, color: {}", 
                featured, limit, color);
        
        List<HomepageActivity> activities;
        
        if ("true".equals(featured)) {
            activities = activityRepository.findFeaturedPublishedActivities();
        } else if (color != null && !color.trim().isEmpty()) {
            activities = activityRepository.findByColorAndIsPublishedTrueOrderByOrderIndexAsc(color);
        } else {
            activities = activityRepository.findPublishedActivitiesOrdered();
        }
        
        // Apply limit if specified
        if (limit != null && limit > 0 && activities.size() > limit) {
            activities = activities.subList(0, limit);
        }
        
        List<ActivityDto> activityDtos = activities.stream()
                .map(this::convertActivityToDto)
                .collect(Collectors.toList());
        
        // Get activities section metadata
        HomepageSection activitiesSection = sectionRepository
                .findBySectionTypeAndIsPublishedTrue(HomepageSection.SectionType.ACTIVITIES)
                .orElse(null);
        
        ActivitiesSectionDto result = new ActivitiesSectionDto();
        if (activitiesSection != null) {
            result.setId(activitiesSection.getId());
            result.setTitle(activitiesSection.getTitle());
            result.setSubtitle(activitiesSection.getSubtitle());
            result.setBackgroundColor(activitiesSection.getBackgroundColor());
            result.setCreatedAt(activitiesSection.getCreatedAt());
            result.setUpdatedAt(activitiesSection.getUpdatedAt());
        } else {
            // Default values if no section found
            result.setTitle("Main Activities of the Project");
            result.setBackgroundColor("white");
        }
        
        // Set default responsive columns
        Map<String, Integer> columns = new HashMap<>();
        columns.put("mobile", 1);
        columns.put("tablet", 2);
        columns.put("desktop", 3);
        result.setColumns(columns);
        
        result.setActivities(activityDtos);
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public OutcomesSectionDto getOutcomesContent() {
        log.debug("Getting outcomes content");
        
        List<HomepageOutcome> outcomes = outcomeRepository.findPublishedOutcomesOrdered();
        
        List<OutcomeDto> outcomeDtos = outcomes.stream()
                .map(this::convertOutcomeToDto)
                .collect(Collectors.toList());
        
        // Calculate overall progress
        double overallProgress = outcomes.stream()
                .filter(o -> o.getMetrics() != null && o.getMetrics().containsKey("percentage"))
                .mapToDouble(o -> {
                    Object percentage = o.getMetrics().get("percentage");
                    if (percentage instanceof Number) {
                        return ((Number) percentage).doubleValue();
                    }
                    return 0.0;
                })
                .average()
                .orElse(0.0);
        
        // Get outcomes section metadata
        HomepageSection outcomesSection = sectionRepository
                .findBySectionTypeAndIsPublishedTrue(HomepageSection.SectionType.OUTCOMES)
                .orElse(null);
        
        OutcomesSectionDto result = new OutcomesSectionDto();
        if (outcomesSection != null) {
            result.setId(outcomesSection.getId());
            result.setTitle(outcomesSection.getTitle());
            result.setDescription(outcomesSection.getDescription());
            result.setBackgroundColor(outcomesSection.getBackgroundColor());
            result.setContentBackground(outcomesSection.getContentBackground());
            result.setCreatedAt(outcomesSection.getCreatedAt());
            result.setUpdatedAt(outcomesSection.getUpdatedAt());
        } else {
            // Default values
            result.setTitle("Proposed Project Outcomes");
            result.setDescription("After the project implementation, it is expected to yield the following outcomes:");
            result.setBackgroundColor("gray-100");
            result.setContentBackground("white");
        }
        
        result.setOutcomes(outcomeDtos);
        result.setOverallProgress(overallProgress);
        result.setShowMetrics(true);
        
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SectionContentDto getSectionContent(String section) {
        log.debug("Getting section content for: {}", section);
        
        try {
            HomepageSection.SectionType sectionType = HomepageSection.SectionType.fromString(section);
            HomepageSection sectionEntity = sectionRepository
                    .findBySectionTypeAndIsPublishedTrue(sectionType)
                    .orElse(null);
            
            if (sectionEntity == null) {
                return null;
            }
            
            return convertSectionToDto(sectionEntity);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid section type: {}", section);
            return null;
        }
    }
    
    // Continue with more methods in the next part...
    
    @Override
    public HeroSectionDto updateHeroContent(HeroSectionDto updateDto) {
        log.debug("Updating hero content");
        
        HomepageHero hero = heroRepository.findLatestHero()
                .orElse(new HomepageHero());
        
        if (updateDto.getTitle() != null) {
            hero.setTitle(updateDto.getTitle());
        }
        if (updateDto.getSubtitle() != null) {
            hero.setSubtitle(updateDto.getSubtitle());
        }
        if (updateDto.getBackgroundImage() != null) {
            hero.setBackgroundImage(updateDto.getBackgroundImage());
        }
        if (updateDto.getBackgroundGradient() != null) {
            hero.setBackgroundGradient(updateDto.getBackgroundGradient());
        }
        if (updateDto.getCtaText() != null) {
            hero.setCtaText(updateDto.getCtaText());
        }
        if (updateDto.getCtaLink() != null) {
            hero.setCtaLink(updateDto.getCtaLink());
        }
        if (updateDto.getCtaColor() != null) {
            hero.setCtaColor(updateDto.getCtaColor());
        }
        if (updateDto.getSearchPlaceholder() != null) {
            hero.setSearchPlaceholder(updateDto.getSearchPlaceholder());
        }
        if (updateDto.getSearchEnabled() != null) {
            hero.setSearchEnabled(updateDto.getSearchEnabled());
        }
        if (updateDto.getAnimations() != null) {
            hero.setAnimations(updateDto.getAnimations());
        }
        if (updateDto.getResponsiveSettings() != null) {
            hero.setResponsiveSettings(updateDto.getResponsiveSettings());
        }
        if (updateDto.getIsPublished() != null) {
            hero.setIsPublished(updateDto.getIsPublished());
        }
        
        HomepageHero savedHero = heroRepository.save(hero);
        return convertHeroToDto(savedHero);
    }
    
    @Override
    public ActivityDto createActivity(CreateActivityDto createDto) {
        log.debug("Creating new activity: {}", createDto.getTitle());
        
        HomepageActivity activity = new HomepageActivity();
        activity.setTitle(createDto.getTitle());
        activity.setDescription(createDto.getDescription());
        activity.setIconClass(createDto.getIconClass());
        activity.setColor(createDto.getColor());
        
        // Set order index
        if (createDto.getOrder() != null) {
            activity.setOrderIndex(createDto.getOrder());
        } else {
            // Auto-assign next order
            Integer maxOrder = activityRepository.findMaxOrderIndex();
            activity.setOrderIndex(maxOrder + 1);
        }
        
        activity.setIsFeatured(createDto.getIsFeatured() != null ? createDto.getIsFeatured() : false);
        activity.setIsPublished(true);
        activity.setAdditionalInfo(createDto.getAdditionalInfo());
        activity.setLink(createDto.getLink());
        activity.setImage(createDto.getImage());
        activity.setTags(createDto.getTags());
        activity.setProgress(createDto.getProgress());
        
        HomepageActivity savedActivity = activityRepository.save(activity);
        return convertActivityToDto(savedActivity);
    }
    
    @Override
    public ActivityDto updateActivity(Long id, UpdateActivityDto updateDto) {
        log.debug("Updating activity with id: {}", id);
        
        HomepageActivity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found with id: " + id));
        
        if (updateDto.getTitle() != null) {
            activity.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            activity.setDescription(updateDto.getDescription());
        }
        if (updateDto.getIconClass() != null) {
            activity.setIconClass(updateDto.getIconClass());
        }
        if (updateDto.getColor() != null) {
            activity.setColor(updateDto.getColor());
        }
        if (updateDto.getOrder() != null) {
            activity.setOrderIndex(updateDto.getOrder());
        }
        if (updateDto.getIsFeatured() != null) {
            activity.setIsFeatured(updateDto.getIsFeatured());
        }
        if (updateDto.getIsPublished() != null) {
            activity.setIsPublished(updateDto.getIsPublished());
        }
        if (updateDto.getAdditionalInfo() != null) {
            activity.setAdditionalInfo(updateDto.getAdditionalInfo());
        }
        if (updateDto.getLink() != null) {
            activity.setLink(updateDto.getLink());
        }
        if (updateDto.getImage() != null) {
            activity.setImage(updateDto.getImage());
        }
        if (updateDto.getTags() != null) {
            activity.setTags(updateDto.getTags());
        }
        if (updateDto.getProgress() != null) {
            activity.setProgress(updateDto.getProgress());
        }
        
        HomepageActivity savedActivity = activityRepository.save(activity);
        return convertActivityToDto(savedActivity);
    }
    
    @Override
    public void deleteActivity(Long id) {
        log.debug("Deleting activity with id: {}", id);
        
        if (!activityRepository.existsById(id)) {
            throw new EntityNotFoundException("Activity not found with id: " + id);
        }
        
        activityRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ActivityDto> getAllActivities() {
        log.debug("Getting all activities");
        
        List<HomepageActivity> activities = activityRepository.findPublishedActivitiesOrdered();
        return activities.stream()
                .map(this::convertActivityToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public OutcomeDto createOutcome(CreateOutcomeDto createDto) {
        log.debug("Creating new outcome: {}", createDto.getTitle());
        
        HomepageOutcome outcome = new HomepageOutcome();
        outcome.setTitle(createDto.getTitle());
        outcome.setDescription(createDto.getDescription());
        outcome.setIconClass(createDto.getIconClass());
        
        // Set order index
        if (createDto.getOrder() != null) {
            outcome.setOrderIndex(createDto.getOrder());
        } else {
            // Auto-assign next order
            Integer maxOrder = outcomeRepository.findMaxOrderIndex();
            outcome.setOrderIndex(maxOrder + 1);
        }
        
        outcome.setIsPublished(true);
        outcome.setMetrics(createDto.getMetrics());
        
        // Set status
        if (createDto.getStatus() != null) {
            try {
                outcome.setStatus(HomepageOutcome.OutcomeStatus.valueOf(
                    createDto.getStatus().toUpperCase().replace("-", "_")));
            } catch (IllegalArgumentException e) {
                outcome.setStatus(HomepageOutcome.OutcomeStatus.ON_TRACK);
            }
        } else {
            outcome.setStatus(HomepageOutcome.OutcomeStatus.ON_TRACK);
        }
        
        outcome.setMilestones(createDto.getMilestones());
        
        HomepageOutcome savedOutcome = outcomeRepository.save(outcome);
        return convertOutcomeToDto(savedOutcome);
    }
    
    @Override
    public OutcomeDto updateOutcome(Long id, UpdateOutcomeDto updateDto) {
        log.debug("Updating outcome with id: {}", id);
        
        HomepageOutcome outcome = outcomeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Outcome not found with id: " + id));
        
        if (updateDto.getTitle() != null) {
            outcome.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            outcome.setDescription(updateDto.getDescription());
        }
        if (updateDto.getIconClass() != null) {
            outcome.setIconClass(updateDto.getIconClass());
        }
        if (updateDto.getOrder() != null) {
            outcome.setOrderIndex(updateDto.getOrder());
        }
        if (updateDto.getIsPublished() != null) {
            outcome.setIsPublished(updateDto.getIsPublished());
        }
        if (updateDto.getMetrics() != null) {
            outcome.setMetrics(updateDto.getMetrics());
        }
        if (updateDto.getStatus() != null) {
            try {
                outcome.setStatus(HomepageOutcome.OutcomeStatus.valueOf(
                    updateDto.getStatus().toUpperCase().replace("-", "_")));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}, keeping current status", updateDto.getStatus());
            }
        }
        if (updateDto.getMilestones() != null) {
            outcome.setMilestones(updateDto.getMilestones());
        }
        
        HomepageOutcome savedOutcome = outcomeRepository.save(outcome);
        return convertOutcomeToDto(savedOutcome);
    }
    
    @Override
    public void deleteOutcome(Long id) {
        log.debug("Deleting outcome with id: {}", id);
        
        if (!outcomeRepository.existsById(id)) {
            throw new EntityNotFoundException("Outcome not found with id: " + id);
        }
        
        outcomeRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OutcomeDto> getAllOutcomes() {
        log.debug("Getting all outcomes");
        
        List<HomepageOutcome> outcomes = outcomeRepository.findPublishedOutcomesOrdered();
        return outcomes.stream()
                .map(this::convertOutcomeToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public SectionContentDto updateSectionContent(String section, UpdateSectionDto updateDto) {
        log.debug("Updating section content for: {}", section);
        
        try {
            HomepageSection.SectionType sectionType = HomepageSection.SectionType.fromString(section);
            
            HomepageSection sectionEntity = sectionRepository.findBySectionType(sectionType)
                    .orElse(new HomepageSection());
            
            // If new entity, set section type
            if (sectionEntity.getId() == null) {
                sectionEntity.setSectionType(sectionType);
            }
            
            if (updateDto.getTitle() != null) {
                sectionEntity.setTitle(updateDto.getTitle());
            }
            if (updateDto.getContent() != null) {
                sectionEntity.setDescription(updateDto.getContent());
            }
            // Note: HomepageSection doesn't have buttonText/buttonLink fields
            // TODO: Add these fields to the entity if needed
            if (updateDto.getBackgroundColor() != null) {
                sectionEntity.setBackgroundColor(updateDto.getBackgroundColor());
            }
            // Note: HomepageSection doesn't have textColor field, only contentBackground
            if (updateDto.getIsActive() != null) {
                sectionEntity.setIsPublished(updateDto.getIsActive());
            }
            // Note: HomepageSection doesn't have displayOrder field
            // TODO: Add displayOrder field if needed
            
            HomepageSection savedSection = sectionRepository.save(sectionEntity);
            return convertSectionToDto(savedSection);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid section type: " + section);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> searchContent(String query, String section, Integer limit) {
        log.debug("Searching content with query: {}, section: {}, limit: {}", query, section, limit);
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        // Search in activities
        if (section == null || "activities".equals(section)) {
            List<HomepageActivity> activities = activityRepository.findBySearchTerm(query);
            for (HomepageActivity activity : activities) {
                Map<String, Object> result = new HashMap<>();
                result.put("type", "activity");
                result.put("id", activity.getId());
                result.put("title", activity.getTitle());
                result.put("description", activity.getDescription());
                result.put("section", "activities");
                result.put("relevance_score", calculateRelevanceScore(query, activity.getTitle(), activity.getDescription()));
                result.put("url", "/activities/" + activity.getId());
                results.add(result);
            }
        }
        
        // Search in outcomes
        if (section == null || "outcomes".equals(section)) {
            List<HomepageOutcome> outcomes = outcomeRepository.findBySearchTerm(query);
            for (HomepageOutcome outcome : outcomes) {
                Map<String, Object> result = new HashMap<>();
                result.put("type", "outcome");
                result.put("id", outcome.getId());
                result.put("title", outcome.getTitle());
                result.put("description", outcome.getDescription());
                result.put("section", "outcomes");
                result.put("relevance_score", calculateRelevanceScore(query, outcome.getTitle(), outcome.getDescription()));
                result.put("url", "/outcomes/" + outcome.getId());
                results.add(result);
            }
        }
        
        // Search in sections
        if (section == null || Arrays.asList("monitoring", "ethics").contains(section)) {
            List<HomepageSection> sections = sectionRepository.findBySearchTerm(query);
            for (HomepageSection sectionEntity : sections) {
                Map<String, Object> result = new HashMap<>();
                result.put("type", "section");
                result.put("id", sectionEntity.getId());
                result.put("title", sectionEntity.getTitle());
                result.put("description", sectionEntity.getDescription());
                result.put("section", sectionEntity.getSectionType().getValue());
                result.put("relevance_score", calculateRelevanceScore(query, sectionEntity.getTitle(), sectionEntity.getDescription()));
                result.put("url", "/" + sectionEntity.getSectionType().getValue());
                results.add(result);
            }
        }
        
        // Sort by relevance score
        results.sort((a, b) -> Double.compare(
            (Double) b.get("relevance_score"), 
            (Double) a.get("relevance_score")
        ));
        
        // Apply limit
        if (limit != null && limit > 0 && results.size() > limit) {
            results = results.subList(0, limit);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("query", query);
        response.put("total_results", results.size());
        response.put("results", results);
        response.put("suggestions", generateSearchSuggestions(query));
        
        return response;
    }
    
    @Override
    public void reorderActivities(List<Long> itemIds) {
        log.debug("Reordering {} activities", itemIds.size());
        
        for (int i = 0; i < itemIds.size(); i++) {
            Long activityId = itemIds.get(i);
            HomepageActivity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new EntityNotFoundException("Activity not found with id: " + activityId));
            activity.setOrderIndex(i + 1);
            activityRepository.save(activity);
        }
    }
    
    @Override
    public void reorderOutcomes(List<Long> itemIds) {
        log.debug("Reordering {} outcomes", itemIds.size());
        
        for (int i = 0; i < itemIds.size(); i++) {
            Long outcomeId = itemIds.get(i);
            HomepageOutcome outcome = outcomeRepository.findById(outcomeId)
                    .orElseThrow(() -> new EntityNotFoundException("Outcome not found with id: " + outcomeId));
            outcome.setOrderIndex(i + 1);
            outcomeRepository.save(outcome);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAnalytics() {
        log.debug("Getting homepage analytics");
        
        Map<String, Object> analytics = new HashMap<>();
        
        // Basic counts
        analytics.put("total_views", 15420L); // Mock data - would come from analytics service
        analytics.put("average_time_on_page", 145);
        analytics.put("bounce_rate", 23.5);
        
        // Section engagement
        Map<String, Object> sectionEngagement = new HashMap<>();
        
        Map<String, Object> heroEngagement = new HashMap<>();
        heroEngagement.put("views", 15420);
        heroEngagement.put("interactions", 3205);
        heroEngagement.put("cta_clicks", 1890);
        sectionEngagement.put("hero", heroEngagement);
        
        Map<String, Object> activitiesEngagement = new HashMap<>();
        activitiesEngagement.put("views", 12380);
        activitiesEngagement.put("card_clicks", 2340);
        activitiesEngagement.put("most_viewed", "Teacher Training");
        sectionEngagement.put("activities", activitiesEngagement);
        
        Map<String, Object> outcomesEngagement = new HashMap<>();
        outcomesEngagement.put("views", 8920);
        outcomesEngagement.put("time_spent", 89);
        outcomesEngagement.put("most_engaging", "Increased Science Teachers");
        sectionEngagement.put("outcomes", outcomesEngagement);
        
        analytics.put("section_engagement", sectionEngagement);
        
        // Device breakdown
        Map<String, Double> deviceBreakdown = new HashMap<>();
        deviceBreakdown.put("mobile", 45.2);
        deviceBreakdown.put("tablet", 23.8);
        deviceBreakdown.put("desktop", 31.0);
        analytics.put("device_breakdown", deviceBreakdown);
        
        // Geographic data
        Map<String, Object> geographicData = new HashMap<>();
        List<Map<String, Object>> topRegions = new ArrayList<>();
        
        Map<String, Object> region1 = new HashMap<>();
        region1.put("region", "Dodoma");
        region1.put("percentage", 35.2);
        topRegions.add(region1);
        
        Map<String, Object> region2 = new HashMap<>();
        region2.put("region", "Dar es Salaam");
        region2.put("percentage", 28.7);
        topRegions.add(region2);
        
        Map<String, Object> region3 = new HashMap<>();
        region3.put("region", "Arusha");
        region3.put("percentage", 15.3);
        topRegions.add(region3);
        
        geographicData.put("top_regions", topRegions);
        analytics.put("geographic_data", geographicData);
        
        analytics.put("last_updated", LocalDateTime.now());
        
        return analytics;
    }
    
    // Helper methods
    private double calculateRelevanceScore(String query, String title, String description) {
        String lowerQuery = query.toLowerCase();
        String lowerTitle = title != null ? title.toLowerCase() : "";
        String lowerDescription = description != null ? description.toLowerCase() : "";
        
        double score = 0.0;
        
        // Title match has higher weight
        if (lowerTitle.contains(lowerQuery)) {
            score += 0.8;
            if (lowerTitle.equals(lowerQuery)) {
                score += 0.2; // Exact match bonus
            }
        }
        
        // Description match
        if (lowerDescription.contains(lowerQuery)) {
            score += 0.3;
        }
        
        // Word boundary matches (individual words)
        String[] queryWords = lowerQuery.split("\\s+");
        for (String word : queryWords) {
            if (lowerTitle.contains(word)) {
                score += 0.1;
            }
            if (lowerDescription.contains(word)) {
                score += 0.05;
            }
        }
        
        return Math.min(score, 1.0); // Cap at 1.0
    }
    
    private List<String> generateSearchSuggestions(String query) {
        // Simple suggestion generation - could be enhanced with more sophisticated algorithms
        List<String> suggestions = new ArrayList<>();
        
        if (query.toLowerCase().contains("teacher")) {
            suggestions.add("teacher development");
            suggestions.add("teacher training");
        }
        if (query.toLowerCase().contains("science")) {
            suggestions.add("science education");
            suggestions.add("science laboratory");
        }
        if (query.toLowerCase().contains("student")) {
            suggestions.add("student engagement");
            suggestions.add("student outcomes");
        }
        
        // Default suggestions
        if (suggestions.isEmpty()) {
            suggestions.add("teacher development");
            suggestions.add("science education");
            suggestions.add("capacity building");
        }
        
        return suggestions;
    }
    
    // Helper conversion methods
    private HeroSectionDto convertHeroToDto(HomepageHero hero) {
        HeroSectionDto dto = new HeroSectionDto();
        dto.setId(hero.getId());
        dto.setTitle(hero.getTitle());
        dto.setSubtitle(hero.getSubtitle());
        dto.setBackgroundImage(hero.getBackgroundImage());
        dto.setBackgroundGradient(hero.getBackgroundGradient());
        dto.setCtaText(hero.getCtaText());
        dto.setCtaLink(hero.getCtaLink());
        dto.setCtaColor(hero.getCtaColor());
        dto.setSearchPlaceholder(hero.getSearchPlaceholder());
        dto.setSearchEnabled(hero.getSearchEnabled());
        dto.setAnimations(hero.getAnimations());
        dto.setResponsiveSettings(hero.getResponsiveSettings());
        dto.setIsPublished(hero.getIsPublished());
        dto.setCreatedAt(hero.getCreatedAt());
        dto.setUpdatedAt(hero.getUpdatedAt());
        return dto;
    }
    
    private ActivityDto convertActivityToDto(HomepageActivity activity) {
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setIconClass(activity.getIconClass());
        dto.setColor(activity.getColor());
        dto.setOrder(activity.getOrderIndex());
        dto.setIsFeatured(activity.getIsFeatured());
        dto.setIsPublished(activity.getIsPublished());
        dto.setAdditionalInfo(activity.getAdditionalInfo());
        dto.setLink(activity.getLink());
        dto.setImage(activity.getImage());
        dto.setTags(activity.getTags());
        dto.setProgress(activity.getProgress());
        dto.setCreatedAt(activity.getCreatedAt());
        dto.setUpdatedAt(activity.getUpdatedAt());
        return dto;
    }
    
    private OutcomeDto convertOutcomeToDto(HomepageOutcome outcome) {
        OutcomeDto dto = new OutcomeDto();
        dto.setId(outcome.getId());
        dto.setTitle(outcome.getTitle());
        dto.setDescription(outcome.getDescription());
        dto.setIconClass(outcome.getIconClass());
        dto.setOrder(outcome.getOrderIndex());
        dto.setIsPublished(outcome.getIsPublished());
        dto.setMetrics(outcome.getMetrics());
        dto.setStatus(outcome.getStatus() != null ? outcome.getStatus().getValue() : "on-track");
        dto.setMilestones(outcome.getMilestones());
        dto.setCreatedAt(outcome.getCreatedAt());
        dto.setUpdatedAt(outcome.getUpdatedAt());
        return dto;
    }
    
    private SectionContentDto convertSectionToDto(HomepageSection section) {
        SectionContentDto dto = new SectionContentDto();
        dto.setId(section.getId());
        dto.setSectionType(section.getSectionType().getValue());
        dto.setTitle(section.getTitle());
        dto.setSubtitle(section.getSubtitle());
        dto.setDescription(section.getDescription());
        dto.setBackgroundColor(section.getBackgroundColor());
        dto.setContentBackground(section.getContentBackground());
        dto.setAdditionalData(section.getAdditionalData());
        dto.setIsPublished(section.getIsPublished());
        dto.setCreatedAt(section.getCreatedAt());
        dto.setUpdatedAt(section.getUpdatedAt());
        return dto;
    }
}
