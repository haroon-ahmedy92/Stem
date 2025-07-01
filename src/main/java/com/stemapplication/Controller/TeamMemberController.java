package com.stemapplication.Controller;

import com.stemapplication.DTO.*;
import com.stemapplication.Service.TeamMemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/team-members")
public class TeamMemberController {
    
    private final TeamMemberService teamMemberService;
    
    @Autowired
    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }
    
    /**
     * GET /api/team-members - Public endpoint (no authentication required)
     * Retrieves all team members with optional search and pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<TeamMembersResponseDto>> getAllTeamMembers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String order) {
        
        try {
            log.debug("Getting all team members - search: {}, limit: {}, offset: {}", search, limit, offset);
            
            // Validate pagination parameters
            if (limit > 100) limit = 100; // Max limit to prevent abuse
            if (limit < 1) limit = 20;
            if (offset < 0) offset = 0;
            
            // Create sort direction
            Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
            
            // Validate sort field
            String sortField = validateSortField(sort);
            
            // Create pageable object
            Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(direction, sortField));
            
            // Get team members
            Page<TeamMemberDto> teamMembersPage = teamMemberService.getAllTeamMembers(search, pageable);
            
            // Create pagination info
            PaginationDto pagination = new PaginationDto(
                    teamMembersPage.getTotalElements(),
                    limit,
                    offset,
                    teamMembersPage.hasNext(),
                    teamMembersPage.hasPrevious()
            );
            
            // Create response data
            TeamMembersResponseDto responseData = new TeamMembersResponseDto(
                    teamMembersPage.getContent(),
                    pagination
            );
            
            ApiResponseDto<TeamMembersResponseDto> response = ApiResponseDto.success(
                    responseData,
                    "Team members retrieved successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error retrieving team members", e);
            ApiResponseDto<TeamMembersResponseDto> errorResponse = ApiResponseDto.error(
                    "Error retrieving team members: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * GET /api/team-members/{id} - Public endpoint (no authentication required)
     * Retrieves a specific team member by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<TeamMemberDto>> getTeamMemberById(@PathVariable Long id) {
        try {
            log.debug("Getting team member by ID: {}", id);
            
            TeamMemberDto teamMember = teamMemberService.getTeamMemberById(id);
            
            ApiResponseDto<TeamMemberDto> response = ApiResponseDto.success(
                    teamMember,
                    "Team member retrieved successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.warn("Team member not found: {}", id);
            ApiResponseDto<TeamMemberDto> errorResponse = ApiResponseDto.error("Team member not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error retrieving team member by ID: {}", id, e);
            ApiResponseDto<TeamMemberDto> errorResponse = ApiResponseDto.error(
                    "Error retrieving team member: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * POST /api/team-members - Secured endpoint (Admin authentication required)
     * Creates a new team member
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<TeamMemberDto>> createTeamMember(
            @Valid @RequestBody CreateTeamMemberDto createDto) {
        
        try {
            log.debug("Creating new team member: {}", createDto.getName());
            
            TeamMemberDto createdTeamMember = teamMemberService.createTeamMember(createDto);
            
            ApiResponseDto<TeamMemberDto> response = ApiResponseDto.success(
                    createdTeamMember,
                    "Team member created successfully"
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error creating team member: {}", e.getMessage());
            ApiResponseDto<TeamMemberDto> errorResponse = ApiResponseDto.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error creating team member", e);
            ApiResponseDto<TeamMemberDto> errorResponse = ApiResponseDto.error(
                    "Error creating team member: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * PUT /api/team-members/{id} - Secured endpoint (Admin authentication required)
     * Updates an existing team member
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<TeamMemberDto>> updateTeamMember(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeamMemberDto updateDto) {
        
        try {
            log.debug("Updating team member with ID: {}", id);
            
            TeamMemberDto updatedTeamMember = teamMemberService.updateTeamMember(id, updateDto);
            
            ApiResponseDto<TeamMemberDto> response = ApiResponseDto.success(
                    updatedTeamMember,
                    "Team member updated successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.warn("Team member not found for update: {}", id);
            ApiResponseDto<TeamMemberDto> errorResponse = ApiResponseDto.error("Team member not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("Validation error updating team member: {}", e.getMessage());
            ApiResponseDto<TeamMemberDto> errorResponse = ApiResponseDto.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error updating team member with ID: {}", id, e);
            ApiResponseDto<TeamMemberDto> errorResponse = ApiResponseDto.error(
                    "Error updating team member: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * DELETE /api/team-members/{id} - Secured endpoint (Admin authentication required)
     * Deletes a team member
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Map<String, String>>> deleteTeamMember(@PathVariable Long id) {
        try {
            log.debug("Deleting team member with ID: {}", id);
            
            teamMemberService.deleteTeamMember(id);
            
            ApiResponseDto<Map<String, String>> response = ApiResponseDto.success(
                    null,
                    "Team member deleted successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.warn("Team member not found for deletion: {}", id);
            ApiResponseDto<Map<String, String>> errorResponse = ApiResponseDto.error("Team member not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error deleting team member with ID: {}", id, e);
            ApiResponseDto<Map<String, String>> errorResponse = ApiResponseDto.error(
                    "Error deleting team member: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Validates and sanitizes the sort field parameter
     */
    private String validateSortField(String sort) {
        return switch (sort.toLowerCase()) {
            case "name" -> "name";
            case "role" -> "role";
            case "qualification" -> "qualification";
            case "createdat", "created_at" -> "createdAt";
            case "updatedat", "updated_at" -> "updatedAt";
            default -> "name"; // Default fallback
        };
    }
}
