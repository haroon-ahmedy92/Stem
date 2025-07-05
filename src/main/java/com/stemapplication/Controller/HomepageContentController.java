package com.stemapplication.Controller;

import com.stemapplication.DTO.*;
import com.stemapplication.Service.HomepageContentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/homepage-content")
public class HomepageContentController {
    
    private final HomepageContentService homepageContentService;
    
    @Autowired
    public HomepageContentController(HomepageContentService homepageContentService) {
        this.homepageContentService = homepageContentService;
    }
    
    // PUBLIC ENDPOINTS (No authentication required)
    
    /**
     * Get all homepage content
     * GET /api/homepage-content
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHomepageContent() {
        try {
            log.debug("Getting all homepage content");
            HomepageContentResponseDto content = homepageContentService.getAllHomepageContent();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", content);
            response.put("message", "Homepage content retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving homepage content", e);
            return createErrorResponse("Failed to retrieve homepage content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get hero section content
     * GET /api/homepage-content/hero
     */
    @GetMapping("/hero")
    public ResponseEntity<Map<String, Object>> getHeroContent() {
        try {
            log.debug("Getting hero content");
            HeroSectionDto hero = homepageContentService.getHeroContent();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", hero);
            response.put("message", "Hero content retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving hero content", e);
            return createErrorResponse("Failed to retrieve hero content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get activities section
     * GET /api/homepage-content/activities
     */
    @GetMapping("/activities")
    public ResponseEntity<Map<String, Object>> getActivitiesContent(
            @RequestParam(value = "featured", required = false) String featured,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "color", required = false) String color) {
        try {
            log.debug("Getting activities content with filters - featured: {}, limit: {}, color: {}", 
                    featured, limit, color);
            
            ActivitiesSectionDto activities = homepageContentService.getActivitiesContent(featured, limit, color);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", activities);
            response.put("message", "Activities retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving activities content", e);
            return createErrorResponse("Failed to retrieve activities content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get outcomes section
     * GET /api/homepage-content/outcomes
     */
    @GetMapping("/outcomes")
    public ResponseEntity<Map<String, Object>> getOutcomesContent() {
        try {
            log.debug("Getting outcomes content");
            OutcomesSectionDto outcomes = homepageContentService.getOutcomesContent();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", outcomes);
            response.put("message", "Outcomes retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving outcomes content", e);
            return createErrorResponse("Failed to retrieve outcomes content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get section content (monitoring, ethics)
     * GET /api/homepage-content/{section}
     */
    @GetMapping("/{section}")
    public ResponseEntity<Map<String, Object>> getSectionContent(@PathVariable String section) {
        try {
            log.debug("Getting section content for: {}", section);
            
            // Check if it's a valid section
            if (!List.of("monitoring", "ethics").contains(section.toLowerCase())) {
                return createErrorResponse("Invalid section. Valid sections: monitoring, ethics", HttpStatus.BAD_REQUEST);
            }
            
            SectionContentDto sectionContent = homepageContentService.getSectionContent(section);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", sectionContent);
            response.put("message", "Section content retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving section content for: {}", section, e);
            return createErrorResponse("Failed to retrieve section content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Search homepage content
     * GET /api/homepage-content/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchContent(
            @RequestParam("q") String query,
            @RequestParam(value = "section", required = false) String section,
            @RequestParam(value = "limit", required = false) Integer limit) {
        try {
            log.debug("Searching content with query: {}, section: {}, limit: {}", query, section, limit);
            
            if (query == null || query.trim().isEmpty()) {
                return createErrorResponse("Search query is required", HttpStatus.BAD_REQUEST);
            }
            
            Map<String, Object> searchResults = homepageContentService.searchContent(query.trim(), section, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", searchResults);
            response.put("message", "Search completed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching content", e);
            return createErrorResponse("Failed to search content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ADMIN ENDPOINTS (Authentication required)
    
    /**
     * Update hero content
     * PUT /api/homepage-content/hero
     */
    @PutMapping("/hero")
    public ResponseEntity<Map<String, Object>> updateHeroContent(@Valid @RequestBody HeroSectionDto updateDto) {
        try {
            log.debug("Updating hero content");
            HeroSectionDto updatedHero = homepageContentService.updateHeroContent(updateDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedHero);
            response.put("message", "Hero content updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating hero content", e);
            return createErrorResponse("Failed to update hero content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Create new activity
     * POST /api/homepage-content/activities
     */
    @PostMapping("/activities")
    public ResponseEntity<Map<String, Object>> createActivity(@Valid @RequestBody CreateActivityDto createDto) {
        try {
            log.debug("Creating new activity: {}", createDto.getTitle());
            ActivityDto createdActivity = homepageContentService.createActivity(createDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdActivity);
            response.put("message", "Activity created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating activity", e);
            return createErrorResponse("Failed to create activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update activity
     * PUT /api/homepage-content/activities/{id}
     */
    @PutMapping("/activities/{id}")
    public ResponseEntity<Map<String, Object>> updateActivity(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateActivityDto updateDto) {
        try {
            log.debug("Updating activity with id: {}", id);
            ActivityDto updatedActivity = homepageContentService.updateActivity(id, updateDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedActivity);
            response.put("message", "Activity updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating activity with id: {}", id, e);
            if (e.getMessage().contains("not found")) {
                return createErrorResponse("Activity not found", HttpStatus.NOT_FOUND);
            }
            return createErrorResponse("Failed to update activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete activity
     * DELETE /api/homepage-content/activities/{id}
     */
    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Map<String, Object>> deleteActivity(@PathVariable Long id) {
        try {
            log.debug("Deleting activity with id: {}", id);
            homepageContentService.deleteActivity(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Activity deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting activity with id: {}", id, e);
            if (e.getMessage().contains("not found")) {
                return createErrorResponse("Activity not found", HttpStatus.NOT_FOUND);
            }
            return createErrorResponse("Failed to delete activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Create new outcome
     * POST /api/homepage-content/outcomes
     */
    @PostMapping("/outcomes")
    public ResponseEntity<Map<String, Object>> createOutcome(@Valid @RequestBody CreateOutcomeDto createDto) {
        try {
            log.debug("Creating new outcome: {}", createDto.getTitle());
            OutcomeDto createdOutcome = homepageContentService.createOutcome(createDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdOutcome);
            response.put("message", "Outcome created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating outcome", e);
            return createErrorResponse("Failed to create outcome", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update outcome
     * PUT /api/homepage-content/outcomes/{id}
     */
    @PutMapping("/outcomes/{id}")
    public ResponseEntity<Map<String, Object>> updateOutcome(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateOutcomeDto updateDto) {
        try {
            log.debug("Updating outcome with id: {}", id);
            OutcomeDto updatedOutcome = homepageContentService.updateOutcome(id, updateDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedOutcome);
            response.put("message", "Outcome updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating outcome with id: {}", id, e);
            if (e.getMessage().contains("not found")) {
                return createErrorResponse("Outcome not found", HttpStatus.NOT_FOUND);
            }
            return createErrorResponse("Failed to update outcome", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete outcome
     * DELETE /api/homepage-content/outcomes/{id}
     */
    @DeleteMapping("/outcomes/{id}")
    public ResponseEntity<Map<String, Object>> deleteOutcome(@PathVariable Long id) {
        try {
            log.debug("Deleting outcome with id: {}", id);
            homepageContentService.deleteOutcome(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Outcome deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting outcome with id: {}", id, e);
            if (e.getMessage().contains("not found")) {
                return createErrorResponse("Outcome not found", HttpStatus.NOT_FOUND);
            }
            return createErrorResponse("Failed to delete outcome", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update section content (monitoring, ethics)
     * PUT /api/homepage-content/{section}
     */
    @PutMapping("/{section}")
    public ResponseEntity<Map<String, Object>> updateSectionContent(
            @PathVariable String section,
            @Valid @RequestBody UpdateSectionDto updateDto) {
        try {
            log.debug("Updating section content for: {}", section);
            
            // Check if it's a valid section for updates
            if (!List.of("activities", "outcomes", "monitoring", "ethics").contains(section.toLowerCase())) {
                return createErrorResponse("Invalid section for update. Valid sections: activities, outcomes, monitoring, ethics", HttpStatus.BAD_REQUEST);
            }
            
            SectionContentDto updatedSection = homepageContentService.updateSectionContent(section, updateDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedSection);
            response.put("message", "Section content updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid section type: {}", section, e);
            return createErrorResponse("Invalid section type", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error updating section content for: {}", section, e);
            return createErrorResponse("Failed to update section content", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Reorder activities
     * PUT /api/homepage-content/activities/reorder
     */
    @PutMapping("/activities/reorder")
    public ResponseEntity<Map<String, Object>> reorderActivities(@Valid @RequestBody ReorderRequestDto reorderDto) {
        try {
            log.debug("Reordering {} activities", reorderDto.getItemIds().size());
            homepageContentService.reorderActivities(reorderDto.getItemIds());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Activities reordered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error reordering activities", e);
            if (e.getMessage().contains("not found")) {
                return createErrorResponse("One or more activities not found", HttpStatus.NOT_FOUND);
            }
            return createErrorResponse("Failed to reorder activities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Reorder outcomes
     * PUT /api/homepage-content/outcomes/reorder
     */
    @PutMapping("/outcomes/reorder")
    public ResponseEntity<Map<String, Object>> reorderOutcomes(@Valid @RequestBody ReorderRequestDto reorderDto) {
        try {
            log.debug("Reordering {} outcomes", reorderDto.getItemIds().size());
            homepageContentService.reorderOutcomes(reorderDto.getItemIds());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Outcomes reordered successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error reordering outcomes", e);
            if (e.getMessage().contains("not found")) {
                return createErrorResponse("One or more outcomes not found", HttpStatus.NOT_FOUND);
            }
            return createErrorResponse("Failed to reorder outcomes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get analytics
     * GET /api/homepage-content/analytics
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        try {
            log.debug("Getting homepage analytics");
            Map<String, Object> analytics = homepageContentService.getAnalytics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", analytics);
            response.put("message", "Analytics retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving analytics", e);
            return createErrorResponse("Failed to retrieve analytics", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Helper method for error responses
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        
        Map<String, Object> error = new HashMap<>();
        error.put("code", status.value());
        error.put("message", message);
        errorResponse.put("error", error);
        
        return ResponseEntity.status(status).body(errorResponse);
    }
}
