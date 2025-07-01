package com.stemapplication.Service;

import com.stemapplication.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamMemberService {
    
    /**
     * Get all team members with optional search and pagination
     */
    Page<TeamMemberDto> getAllTeamMembers(String searchTerm, Pageable pageable);
    
    /**
     * Get a specific team member by ID
     */
    TeamMemberDto getTeamMemberById(Long id);
    
    /**
     * Create a new team member (Admin only)
     */
    TeamMemberDto createTeamMember(CreateTeamMemberDto createDto);
    
    /**
     * Update an existing team member (Admin only)
     */
    TeamMemberDto updateTeamMember(Long id, UpdateTeamMemberDto updateDto);
    
    /**
     * Delete a team member (Admin only)
     */
    void deleteTeamMember(Long id);
    
    /**
     * Check if team member exists by ID
     */
    boolean existsById(Long id);
    
    /**
     * Check if email is already taken by another team member
     */
    boolean isEmailTaken(String email, Long excludeId);
}
