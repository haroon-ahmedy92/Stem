package com.stemapplication.Service.impl;

import com.stemapplication.DTO.*;
import com.stemapplication.Models.TeamMember;
import com.stemapplication.Repository.TeamMemberRepository;
import com.stemapplication.Service.TeamMemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
public class TeamMemberServiceImpl implements TeamMemberService {
    
    private final TeamMemberRepository teamMemberRepository;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public TeamMemberServiceImpl(TeamMemberRepository teamMemberRepository, ObjectMapper objectMapper) {
        this.teamMemberRepository = teamMemberRepository;
        this.objectMapper = objectMapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TeamMemberDto> getAllTeamMembers(String searchTerm, Pageable pageable) {
        log.debug("Getting all team members with search term: {}", searchTerm);
        
        Page<TeamMember> teamMembersPage;
        
        if (StringUtils.hasText(searchTerm)) {
            teamMembersPage = teamMemberRepository.findBySearchTerm(searchTerm.trim(), pageable);
        } else {
            teamMembersPage = teamMemberRepository.findAll(pageable);
        }
        
        return teamMembersPage.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TeamMemberDto getTeamMemberById(Long id) {
        log.debug("Getting team member by ID: {}", id);
        
        TeamMember teamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found with ID: " + id));
        
        return convertToDto(teamMember);
    }
    
    @Override
    public TeamMemberDto createTeamMember(CreateTeamMemberDto createDto) {
        log.debug("Creating new team member: {}", createDto.getName());
        
        // Check if email is already taken
        if (createDto.getContact() != null && StringUtils.hasText(createDto.getContact().getEmail())) {
            if (teamMemberRepository.existsByEmailIgnoreCase(createDto.getContact().getEmail())) {
                throw new IllegalArgumentException("Email is already in use: " + createDto.getContact().getEmail());
            }
        }
        
        TeamMember teamMember = convertFromCreateDto(createDto);
        TeamMember savedTeamMember = teamMemberRepository.save(teamMember);
        
        log.info("Created team member with ID: {}", savedTeamMember.getId());
        return convertToDto(savedTeamMember);
    }
    
    @Override
    public TeamMemberDto updateTeamMember(Long id, UpdateTeamMemberDto updateDto) {
        log.debug("Updating team member with ID: {}", id);
        
        TeamMember existingTeamMember = teamMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found with ID: " + id));
        
        // Check if email is already taken by another team member
        if (updateDto.getContact() != null && StringUtils.hasText(updateDto.getContact().getEmail())) {
            if (isEmailTaken(updateDto.getContact().getEmail(), id)) {
                throw new IllegalArgumentException("Email is already in use: " + updateDto.getContact().getEmail());
            }
        }
        
        updateTeamMemberFromDto(existingTeamMember, updateDto);
        TeamMember savedTeamMember = teamMemberRepository.save(existingTeamMember);
        
        log.info("Updated team member with ID: {}", savedTeamMember.getId());
        return convertToDto(savedTeamMember);
    }
    
    @Override
    public void deleteTeamMember(Long id) {
        log.debug("Deleting team member with ID: {}", id);
        
        if (!teamMemberRepository.existsById(id)) {
            throw new EntityNotFoundException("Team member not found with ID: " + id);
        }
        
        teamMemberRepository.deleteById(id);
        log.info("Deleted team member with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return teamMemberRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isEmailTaken(String email, Long excludeId) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        
        TeamMember existingMember = teamMemberRepository.findByEmailIgnoreCase(email);
        return existingMember != null && !existingMember.getId().equals(excludeId);
    }
    
    // Manual mapping methods (no MapStruct)
    
    private TeamMemberDto convertToDto(TeamMember teamMember) {
        TeamMemberDto dto = new TeamMemberDto();
        dto.setId(teamMember.getId());
        dto.setName(teamMember.getName());
        dto.setQualification(teamMember.getQualification());
        dto.setRole(teamMember.getRole());
        dto.setProfileImage(teamMember.getProfileImage());
        dto.setBio(teamMember.getBio());
        dto.setLinkedin(teamMember.getLinkedin());
        dto.setCreatedAt(teamMember.getCreatedAt());
        dto.setUpdatedAt(teamMember.getUpdatedAt());
        
        // Map contact information
        ContactDto contactDto = new ContactDto();
        contactDto.setAddress(teamMember.getAddress());
        contactDto.setEmail(teamMember.getEmail());
        contactDto.setPhone(teamMember.getPhone());
        dto.setContact(contactDto);
        
        // Convert JSON strings to objects
        dto.setResearchInterests(parseJsonToStringList(teamMember.getResearchInterests()));
        dto.setPublications(parseJsonToMapList(teamMember.getPublications()));
        
        return dto;
    }
    
    private TeamMember convertFromCreateDto(CreateTeamMemberDto createDto) {
        TeamMember teamMember = new TeamMember();
        teamMember.setName(createDto.getName());
        teamMember.setQualification(createDto.getQualification());
        teamMember.setRole(createDto.getRole());
        teamMember.setProfileImage(createDto.getProfileImage());
        teamMember.setBio(createDto.getBio());
        teamMember.setLinkedin(createDto.getLinkedin());
        
        // Map contact information
        if (createDto.getContact() != null) {
            teamMember.setAddress(createDto.getContact().getAddress());
            teamMember.setEmail(createDto.getContact().getEmail());
            teamMember.setPhone(createDto.getContact().getPhone());
        }
        
        // Convert objects to JSON strings
        teamMember.setResearchInterests(convertToJson(createDto.getResearchInterests()));
        teamMember.setPublications(convertToJson(createDto.getPublications()));
        
        return teamMember;
    }
    
    private void updateTeamMemberFromDto(TeamMember teamMember, UpdateTeamMemberDto updateDto) {
        if (StringUtils.hasText(updateDto.getName())) {
            teamMember.setName(updateDto.getName());
        }
        if (StringUtils.hasText(updateDto.getQualification())) {
            teamMember.setQualification(updateDto.getQualification());
        }
        if (StringUtils.hasText(updateDto.getRole())) {
            teamMember.setRole(updateDto.getRole());
        }
        if (StringUtils.hasText(updateDto.getProfileImage())) {
            teamMember.setProfileImage(updateDto.getProfileImage());
        }
        if (updateDto.getBio() != null) {
            teamMember.setBio(updateDto.getBio());
        }
        if (StringUtils.hasText(updateDto.getLinkedin())) {
            teamMember.setLinkedin(updateDto.getLinkedin());
        }
        
        // Update contact information
        if (updateDto.getContact() != null) {
            if (updateDto.getContact().getAddress() != null) {
                teamMember.setAddress(updateDto.getContact().getAddress());
            }
            if (updateDto.getContact().getEmail() != null) {
                teamMember.setEmail(updateDto.getContact().getEmail());
            }
            if (updateDto.getContact().getPhone() != null) {
                teamMember.setPhone(updateDto.getContact().getPhone());
            }
        }
        
        // Update JSON fields
        if (updateDto.getResearchInterests() != null) {
            teamMember.setResearchInterests(convertToJson(updateDto.getResearchInterests()));
        }
        if (updateDto.getPublications() != null) {
            teamMember.setPublications(convertToJson(updateDto.getPublications()));
        }
    }
    
    // JSON conversion utility methods
    
    private List<String> parseJsonToStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse research interests JSON: {}", json, e);
            return List.of();
        }
    }
    
    private List<Map<String, Object>> parseJsonToMapList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse publications JSON: {}", json, e);
            return List.of();
        }
    }
    
    private String convertToJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert object to JSON: {}", object, e);
            return null;
        }
    }
}
