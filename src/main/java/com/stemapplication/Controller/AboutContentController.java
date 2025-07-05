package com.stemapplication.Controller;

import com.stemapplication.DTO.*;
import com.stemapplication.Service.AboutContentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/about-content")
public class AboutContentController {
    
    private final AboutContentService aboutContentService;
    
    @Autowired
    public AboutContentController(AboutContentService aboutContentService) {
        this.aboutContentService = aboutContentService;
    }
    
    /**
     * GET /api/about-content - Public endpoint (no authentication required)
     * Retrieves all content for the About page
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<AboutContentResponseDto>> getAllAboutContent() {
        try {
            log.debug("Getting all about page content");
            
            AboutContentResponseDto content = aboutContentService.getAllAboutContent();
            
            ApiResponseDto<AboutContentResponseDto> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(content);
            response.setMessage("About content retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving about content", e);
            
            ApiResponseDto<AboutContentResponseDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to retrieve about content: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /api/about-content/{section} - Public endpoint (no authentication required)
     * Retrieves content for a specific section
     */
    @GetMapping("/{section}")
    public ResponseEntity<ApiResponseDto<Object>> getSectionContent(@PathVariable String section) {
        try {
            log.debug("Getting content for section: {}", section);
            
            Object content = aboutContentService.getSectionContent(section);
            
            ApiResponseDto<Object> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(content);
            response.setMessage("Section content retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid section requested: {}", section);
            
            ApiResponseDto<Object> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Invalid section: " + section);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error retrieving section content for: {}", section, e);
            
            ApiResponseDto<Object> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to retrieve section content: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * PUT /api/about-content/{section} - Admin only endpoint
     * Updates content for a specific section
     */
    @PutMapping("/{section}")
    public ResponseEntity<ApiResponseDto<Object>> updateSectionContent(
            @PathVariable String section,
            @RequestBody Object updateDto) {
        try {
            log.debug("Updating content for section: {}", section);
            
            Object updatedContent = aboutContentService.updateSectionContent(section, updateDto);
            
            ApiResponseDto<Object> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(updatedContent);
            response.setMessage("Section content updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid section for update: {}", section);
            
            ApiResponseDto<Object> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Invalid section: " + section);
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error updating section content for: {}", section, e);
            
            ApiResponseDto<Object> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to update section content: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /api/about-content/benefits - Public endpoint (no authentication required)
     * Retrieves all benefits
     */
    @GetMapping("/benefits")
    public ResponseEntity<ApiResponseDto<List<BenefitDto>>> getAllBenefits() {
        try {
            log.debug("Getting all benefits");
            
            List<BenefitDto> benefits = aboutContentService.getAllBenefits();
            
            ApiResponseDto<List<BenefitDto>> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(benefits);
            response.setMessage("Benefits retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving benefits", e);
            
            ApiResponseDto<List<BenefitDto>> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to retrieve benefits: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * POST /api/about-content/benefits - Admin only endpoint
     * Creates a new benefit
     */
    @PostMapping("/benefits")
    public ResponseEntity<ApiResponseDto<BenefitDto>> createBenefit(@Valid @RequestBody CreateBenefitDto createDto) {
        try {
            log.debug("Creating new benefit: {}", createDto.getTitle());
            
            BenefitDto createdBenefit = aboutContentService.createBenefit(createDto);
            
            ApiResponseDto<BenefitDto> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(createdBenefit);
            response.setMessage("Benefit created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error creating benefit", e);
            
            ApiResponseDto<BenefitDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to create benefit: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * PUT /api/about-content/benefits/{id} - Admin only endpoint
     * Updates a specific benefit
     */
    @PutMapping("/benefits/{id}")
    public ResponseEntity<ApiResponseDto<BenefitDto>> updateBenefit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBenefitDto updateDto) {
        try {
            log.debug("Updating benefit with id: {}", id);
            
            BenefitDto updatedBenefit = aboutContentService.updateBenefit(id, updateDto);
            
            ApiResponseDto<BenefitDto> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(updatedBenefit);
            response.setMessage("Benefit updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error updating benefit with id: {}", id, e);
            
            ApiResponseDto<BenefitDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to update benefit: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * DELETE /api/about-content/benefits/{id} - Admin only endpoint
     * Deletes a specific benefit
     */
    @DeleteMapping("/benefits/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBenefit(@PathVariable Long id) {
        try {
            log.debug("Deleting benefit with id: {}", id);
            
            aboutContentService.deleteBenefit(id);
            
            ApiResponseDto<Void> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setMessage("Benefit deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting benefit with id: {}", id, e);
            
            ApiResponseDto<Void> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to delete benefit: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * POST /api/about-content/objectives - Admin only endpoint
     * Creates a new specific objective
     */
    @PostMapping("/objectives")
    public ResponseEntity<ApiResponseDto<SpecificObjectiveDto>> createSpecificObjective(
            @Valid @RequestBody CreateObjectiveDto createDto) {
        try {
            log.debug("Creating new specific objective: {}", createDto.getTitle());
            
            SpecificObjectiveDto createdObjective = aboutContentService.createSpecificObjective(createDto);
            
            ApiResponseDto<SpecificObjectiveDto> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(createdObjective);
            response.setMessage("Specific objective created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error creating specific objective", e);
            
            ApiResponseDto<SpecificObjectiveDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to create specific objective: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /api/about-content/analytics - Admin only endpoint
     * Retrieves analytics data for the About page
     */
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponseDto<Object>> getAnalytics() {
        try {
            log.debug("Getting analytics data");
            
            Object analytics = aboutContentService.getAnalytics();
            
            ApiResponseDto<Object> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(analytics);
            response.setMessage("Analytics retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving analytics", e);
            
            ApiResponseDto<Object> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to retrieve analytics: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // =================== Background Section CRUD Endpoints ===================
    
    /**
     * POST /api/about-content/background/sections - Admin only endpoint
     * Creates a new background section
     */
    @PostMapping("/background/sections")
    public ResponseEntity<ApiResponseDto<BackgroundSectionDto>> createBackgroundSection(
            @Valid @RequestBody CreateBackgroundSectionDto createDto) {
        try {
            log.debug("Creating new background section: {}", createDto.getTitle());
            
            BackgroundSectionDto createdSection = aboutContentService.createBackgroundSection(createDto);
            
            ApiResponseDto<BackgroundSectionDto> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(createdSection);
            response.setMessage("Background section created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for background section creation: {}", e.getMessage());
            
            ApiResponseDto<BackgroundSectionDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Invalid input: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error creating background section", e);
            
            ApiResponseDto<BackgroundSectionDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to create background section: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * PUT /api/about-content/background/sections/{id} - Admin only endpoint
     * Updates an existing background section
     */
    @PutMapping("/background/sections/{id}")
    public ResponseEntity<ApiResponseDto<BackgroundSectionDto>> updateBackgroundSection(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBackgroundSectionDto updateDto) {
        try {
            log.debug("Updating background section with id: {}", id);
            
            BackgroundSectionDto updatedSection = aboutContentService.updateBackgroundSection(id, updateDto);
            
            ApiResponseDto<BackgroundSectionDto> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(updatedSection);
            response.setMessage("Background section updated successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for background section update: {}", e.getMessage());
            
            ApiResponseDto<BackgroundSectionDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Invalid input: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Background section not found with id: {}", id);
                
                ApiResponseDto<BackgroundSectionDto> errorResponse = new ApiResponseDto<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Background section not found with id: " + id);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            log.error("Error updating background section with id: {}", id, e);
            
            ApiResponseDto<BackgroundSectionDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to update background section: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error updating background section with id: {}", id, e);
            
            ApiResponseDto<BackgroundSectionDto> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to update background section: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * DELETE /api/about-content/background/sections/{id} - Admin only endpoint
     * Deletes a background section
     */
    @DeleteMapping("/background/sections/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteBackgroundSection(@PathVariable Long id) {
        try {
            log.debug("Deleting background section with id: {}", id);
            
            aboutContentService.deleteBackgroundSection(id);
            
            ApiResponseDto<Void> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setMessage("Background section deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("Background section not found with id: {}", id);
                
                ApiResponseDto<Void> errorResponse = new ApiResponseDto<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Background section not found with id: " + id);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            log.error("Error deleting background section with id: {}", id, e);
            
            ApiResponseDto<Void> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to delete background section: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error deleting background section with id: {}", id, e);
            
            ApiResponseDto<Void> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to delete background section: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * PUT /api/about-content/background/sections/reorder - Admin only endpoint
     * Reorders background sections by updating their display order
     */
    @PutMapping("/background/sections/reorder")
    public ResponseEntity<ApiResponseDto<List<BackgroundSectionDto>>> reorderBackgroundSections(
            @Valid @RequestBody ReorderBackgroundSectionsDto reorderDto) {
        try {
            log.debug("Reordering background sections with {} items", reorderDto.getSectionsOrder().size());
            
            List<BackgroundSectionDto> reorderedSections = aboutContentService.reorderBackgroundSections(reorderDto);
            
            ApiResponseDto<List<BackgroundSectionDto>> response = new ApiResponseDto<>();
            response.setSuccess(true);
            response.setData(reorderedSections);
            response.setMessage("Background sections reordered successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input for background sections reordering: {}", e.getMessage());
            
            ApiResponseDto<List<BackgroundSectionDto>> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Invalid input: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                log.warn("One or more background sections not found during reordering");
                
                ApiResponseDto<List<BackgroundSectionDto>> errorResponse = new ApiResponseDto<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("One or more background sections not found");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            log.error("Error reordering background sections", e);
            
            ApiResponseDto<List<BackgroundSectionDto>> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to reorder background sections: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error reordering background sections", e);
            
            ApiResponseDto<List<BackgroundSectionDto>> errorResponse = new ApiResponseDto<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Failed to reorder background sections: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
