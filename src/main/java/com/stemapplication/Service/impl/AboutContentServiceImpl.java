package com.stemapplication.Service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stemapplication.DTO.*;
import com.stemapplication.Models.*;
import com.stemapplication.Repository.*;
import com.stemapplication.Service.AboutContentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AboutContentServiceImpl implements AboutContentService {
    
    private final AboutBackgroundRepository backgroundRepository;
    private final AboutBackgroundSectionRepository backgroundSectionRepository;
    private final StemBenefitRepository benefitRepository;
    private final AboutJustificationRepository justificationRepository;
    private final JustificationReferenceRepository referenceRepository;
    private final AboutObjectivesRepository objectivesRepository;
    private final SpecificObjectiveRepository specificObjectiveRepository;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public AboutContentServiceImpl(
            AboutBackgroundRepository backgroundRepository,
            AboutBackgroundSectionRepository backgroundSectionRepository,
            StemBenefitRepository benefitRepository,
            AboutJustificationRepository justificationRepository,
            JustificationReferenceRepository referenceRepository,
            AboutObjectivesRepository objectivesRepository,
            SpecificObjectiveRepository specificObjectiveRepository,
            ObjectMapper objectMapper) {
        this.backgroundRepository = backgroundRepository;
        this.backgroundSectionRepository = backgroundSectionRepository;
        this.benefitRepository = benefitRepository;
        this.justificationRepository = justificationRepository;
        this.referenceRepository = referenceRepository;
        this.objectivesRepository = objectivesRepository;
        this.specificObjectiveRepository = specificObjectiveRepository;
        this.objectMapper = objectMapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public AboutContentResponseDto getAllAboutContent() {
        log.debug("Getting all about page content");
        
        AboutContentResponseDto response = new AboutContentResponseDto();
        
        // Get background
        response.setBackground(getBackgroundContent());
        
        // Get benefits
        response.setBenefits(getAllBenefits());
        
        // Get justification
        response.setJustification(getJustificationContent());
        
        // Get objectives
        response.setObjectives(getObjectivesContent());
        
        response.setLastUpdated(LocalDateTime.now());
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getSectionContent(String section) {
        log.debug("Getting content for section: {}", section);
        
        switch (section.toLowerCase()) {
            case "background":
                return getBackgroundContent();
            case "benefits":
                return getAllBenefits();
            case "justification":
                return getJustificationContent();
            case "objectives":
                return getObjectivesContent();
            default:
                throw new IllegalArgumentException("Invalid section: " + section);
        }
    }
    
    @Override
    public Object updateSectionContent(String section, Object updateDto) {
        log.debug("Updating content for section: {}", section);
        
        try {
            switch (section.toLowerCase()) {
                case "background":
                    BackgroundDto backgroundDto = objectMapper.convertValue(updateDto, BackgroundDto.class);
                    return updateBackgroundContent(backgroundDto);
                case "justification":
                    JustificationDto justificationDto = objectMapper.convertValue(updateDto, JustificationDto.class);
                    return updateJustificationContent(justificationDto);
                case "objectives":
                    ObjectivesDto objectivesDto = objectMapper.convertValue(updateDto, ObjectivesDto.class);
                    return updateObjectivesContent(objectivesDto);
                default:
                    throw new IllegalArgumentException("Invalid section for update: " + section);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error converting DTO for section: {}", section, e);
            throw new RuntimeException("Failed to process update data for section: " + section, e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BenefitDto> getAllBenefits() {
        log.debug("Getting all benefits");
        
        List<StemBenefit> benefits = benefitRepository.findActiveOrderByDisplayOrder();
        return benefits.stream()
                .map(this::convertBenefitToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public BenefitDto createBenefit(CreateBenefitDto createDto) {
        log.debug("Creating new benefit: {}", createDto.getTitle());
        
        StemBenefit benefit = new StemBenefit();
        benefit.setTitle(createDto.getTitle());
        benefit.setDescription(createDto.getDescription());
        benefit.setIcon(createDto.getIcon() != null ? createDto.getIcon() : "fas fa-check-circle");
        benefit.setDisplayOrder(createDto.getDisplayOrder() != null ? createDto.getDisplayOrder() : 0);
        benefit.setIsActive(true);
        
        StemBenefit savedBenefit = benefitRepository.save(benefit);
        return convertBenefitToDto(savedBenefit);
    }
    
    @Override
    public BenefitDto updateBenefit(Long id, UpdateBenefitDto updateDto) {
        log.debug("Updating benefit with id: {}", id);
        
        StemBenefit benefit = benefitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Benefit not found with id: " + id));
        
        if (updateDto.getTitle() != null) {
            benefit.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            benefit.setDescription(updateDto.getDescription());
        }
        if (updateDto.getIcon() != null) {
            benefit.setIcon(updateDto.getIcon());
        }
        if (updateDto.getDisplayOrder() != null) {
            benefit.setDisplayOrder(updateDto.getDisplayOrder());
        }
        if (updateDto.getIsActive() != null) {
            benefit.setIsActive(updateDto.getIsActive());
        }
        
        StemBenefit savedBenefit = benefitRepository.save(benefit);
        return convertBenefitToDto(savedBenefit);
    }
    
    @Override
    public void deleteBenefit(Long id) {
        log.debug("Deleting benefit with id: {}", id);
        
        if (!benefitRepository.existsById(id)) {
            throw new EntityNotFoundException("Benefit not found with id: " + id);
        }
        
        benefitRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SpecificObjectiveDto> getAllSpecificObjectives() {
        log.debug("Getting all specific objectives");
        
        AboutObjectives objectives = objectivesRepository.findAll().stream().findFirst()
                .orElse(null);
        
        if (objectives == null) {
            return List.of();
        }
        
        List<SpecificObjective> specificObjectives = 
                specificObjectiveRepository.findActiveByAboutObjectivesIdOrderByDisplayOrder(objectives.getId());
        
        return specificObjectives.stream()
                .map(this::convertSpecificObjectiveToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public SpecificObjectiveDto createSpecificObjective(CreateObjectiveDto createDto) {
        log.debug("Creating new specific objective: {}", createDto.getTitle());
        
        AboutObjectives objectives = objectivesRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Objectives section not found"));
        
        SpecificObjective objective = new SpecificObjective();
        objective.setTitle(createDto.getTitle());
        objective.setDescription(createDto.getDescription());
        objective.setDisplayOrder(createDto.getDisplayOrder() != null ? createDto.getDisplayOrder() : 0);
        objective.setIsActive(true);
        objective.setAboutObjectives(objectives);
        
        SpecificObjective savedObjective = specificObjectiveRepository.save(objective);
        return convertSpecificObjectiveToDto(savedObjective);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getAnalytics() {
        log.debug("Getting analytics data");
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalBenefits", benefitRepository.count());
        analytics.put("totalObjectives", specificObjectiveRepository.count());
        analytics.put("lastUpdated", LocalDateTime.now());
        
        return analytics;
    }
    
    // Private helper methods
    
    private BackgroundDto getBackgroundContent() {
        AboutBackground background = backgroundRepository.findAll().stream().findFirst()
                .orElse(null);
        
        if (background == null) {
            return null;
        }
        
        BackgroundDto dto = new BackgroundDto();
        dto.setId(background.getId());
        dto.setTitle(background.getTitle());
        dto.setMainContent(background.getMainContent());
        dto.setCtaText(background.getCtaText());
        dto.setCtaLink(background.getCtaLink());
        
        // Get sections
        List<AboutBackgroundSection> sections = 
                backgroundSectionRepository.findByAboutBackgroundIdOrderByDisplayOrder(background.getId());
        
        List<BackgroundSectionDto> sectionDtos = sections.stream()
                .map(this::convertBackgroundSectionToDto)
                .collect(Collectors.toList());
        
        dto.setSections(sectionDtos);
        
        return dto;
    }
    
    private JustificationDto getJustificationContent() {
        AboutJustification justification = justificationRepository.findAll().stream().findFirst()
                .orElse(null);
        
        if (justification == null) {
            return null;
        }
        
        JustificationDto dto = new JustificationDto();
        dto.setId(justification.getId());
        dto.setTitle(justification.getTitle());
        dto.setContent(justification.getContent());
        dto.setConclusion(justification.getConclusion());
        
        // Get references
        List<JustificationReference> references = 
                referenceRepository.findByAboutJustificationIdOrderByDisplayOrder(justification.getId());
        
        List<ReferenceDto> referenceDtos = references.stream()
                .map(this::convertReferenceToDto)
                .collect(Collectors.toList());
        
        dto.setReferences(referenceDtos);
        
        return dto;
    }
    
    private ObjectivesDto getObjectivesContent() {
        AboutObjectives objectives = objectivesRepository.findAll().stream().findFirst()
                .orElse(null);
        
        if (objectives == null) {
            return null;
        }
        
        ObjectivesDto dto = new ObjectivesDto();
        dto.setId(objectives.getId());
        dto.setTitle(objectives.getTitle());
        dto.setIntroduction(objectives.getIntroduction());
        dto.setConclusion(objectives.getConclusion());
        
        // Get specific objectives
        List<SpecificObjective> specificObjectives = 
                specificObjectiveRepository.findActiveByAboutObjectivesIdOrderByDisplayOrder(objectives.getId());
        
        List<SpecificObjectiveDto> specificObjectiveDtos = specificObjectives.stream()
                .map(this::convertSpecificObjectiveToDto)
                .collect(Collectors.toList());
        
        dto.setSpecificObjectives(specificObjectiveDtos);
        
        return dto;
    }
    
    private BackgroundDto updateBackgroundContent(BackgroundDto updateDto) {
        AboutBackground background = backgroundRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Background section not found"));
        
        if (updateDto.getTitle() != null) {
            background.setTitle(updateDto.getTitle());
        }
        if (updateDto.getMainContent() != null) {
            background.setMainContent(updateDto.getMainContent());
        }
        if (updateDto.getCtaText() != null) {
            background.setCtaText(updateDto.getCtaText());
        }
        if (updateDto.getCtaLink() != null) {
            background.setCtaLink(updateDto.getCtaLink());
        }
        
        backgroundRepository.save(background);
        return getBackgroundContent();
    }
    
    private JustificationDto updateJustificationContent(JustificationDto updateDto) {
        AboutJustification justification = justificationRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Justification section not found"));
        
        if (updateDto.getTitle() != null) {
            justification.setTitle(updateDto.getTitle());
        }
        if (updateDto.getContent() != null) {
            justification.setContent(updateDto.getContent());
        }
        if (updateDto.getConclusion() != null) {
            justification.setConclusion(updateDto.getConclusion());
        }
        
        justificationRepository.save(justification);
        return getJustificationContent();
    }
    
    private ObjectivesDto updateObjectivesContent(ObjectivesDto updateDto) {
        AboutObjectives objectives = objectivesRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Objectives section not found"));
        
        if (updateDto.getTitle() != null) {
            objectives.setTitle(updateDto.getTitle());
        }
        if (updateDto.getIntroduction() != null) {
            objectives.setIntroduction(updateDto.getIntroduction());
        }
        if (updateDto.getConclusion() != null) {
            objectives.setConclusion(updateDto.getConclusion());
        }
        
        objectivesRepository.save(objectives);
        return getObjectivesContent();
    }
    
    // DTO Conversion methods
    
    private BenefitDto convertBenefitToDto(StemBenefit benefit) {
        BenefitDto dto = new BenefitDto();
        dto.setId(benefit.getId());
        dto.setTitle(benefit.getTitle());
        dto.setDescription(benefit.getDescription());
        dto.setIcon(benefit.getIcon());
        dto.setDisplayOrder(benefit.getDisplayOrder());
        dto.setIsActive(benefit.getIsActive());
        return dto;
    }
    
    private ReferenceDto convertReferenceToDto(JustificationReference reference) {
        ReferenceDto dto = new ReferenceDto();
        dto.setId(reference.getId());
        dto.setTitle(reference.getTitle());
        dto.setUrl(reference.getUrl());
        dto.setAuthor(reference.getAuthor());
        dto.setPublicationDate(reference.getPublicationDate());
        dto.setDisplayOrder(reference.getDisplayOrder());
        return dto;
    }
    
    @Override
    public BackgroundSectionDto createBackgroundSection(CreateBackgroundSectionDto createDto) {
        log.debug("Creating new background section: {}", createDto.getTitle());
        
        // Get the default background (assuming there's one)
        AboutBackground background = backgroundRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No background found. Please create background first."));
        
        AboutBackgroundSection section = new AboutBackgroundSection();
        section.setTitle(createDto.getTitle());
        section.setContent(createDto.getContent());
        section.setDisplayOrder(createDto.getDisplayOrder() != null ? 
                createDto.getDisplayOrder() : 
                backgroundSectionRepository.getNextDisplayOrder(background.getId()));
        section.setIsActive(createDto.getIsActive() != null ? createDto.getIsActive() : true);
        section.setAboutBackground(background);
        
        AboutBackgroundSection savedSection = backgroundSectionRepository.save(section);
        return convertBackgroundSectionToDto(savedSection);
    }
    
    @Override
    public BackgroundSectionDto updateBackgroundSection(Long id, UpdateBackgroundSectionDto updateDto) {
        log.debug("Updating background section with id: {}", id);
        
        AboutBackgroundSection section = backgroundSectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Background section not found with id: " + id));
        
        if (updateDto.getTitle() != null) {
            section.setTitle(updateDto.getTitle());
        }
        if (updateDto.getContent() != null) {
            section.setContent(updateDto.getContent());
        }
        if (updateDto.getDisplayOrder() != null) {
            section.setDisplayOrder(updateDto.getDisplayOrder());
        }
        if (updateDto.getIsActive() != null) {
            section.setIsActive(updateDto.getIsActive());
        }
        
        AboutBackgroundSection savedSection = backgroundSectionRepository.save(section);
        return convertBackgroundSectionToDto(savedSection);
    }
    
    @Override
    public void deleteBackgroundSection(Long id) {
        log.debug("Deleting background section with id: {}", id);
        
        AboutBackgroundSection section = backgroundSectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Background section not found with id: " + id));
        
        backgroundSectionRepository.delete(section);
        log.info("Background section deleted successfully with id: {}", id);
    }
    
    @Override
    @Transactional
    public List<BackgroundSectionDto> reorderBackgroundSections(ReorderBackgroundSectionsDto reorderDto) {
        log.debug("Reordering background sections");
        
        List<Long> sectionIds = reorderDto.getSectionsOrder();
        
        // Validate that all sections exist
        for (Long sectionId : sectionIds) {
            if (!backgroundSectionRepository.existsById(sectionId)) {
                throw new EntityNotFoundException("Background section not found with id: " + sectionId);
            }
        }
        
        // Update display orders
        for (int i = 0; i < sectionIds.size(); i++) {
            Long sectionId = sectionIds.get(i);
            backgroundSectionRepository.updateDisplayOrder(sectionId, i + 1);
        }
        
        // Return updated sections
        List<AboutBackgroundSection> sections = backgroundSectionRepository.findAllById(sectionIds);
        return sections.stream()
                .sorted((s1, s2) -> s1.getDisplayOrder().compareTo(s2.getDisplayOrder()))
                .map(this::convertBackgroundSectionToDto)
                .collect(Collectors.toList());
    }
    
    private BackgroundSectionDto convertBackgroundSectionToDto(AboutBackgroundSection section) {
        BackgroundSectionDto dto = new BackgroundSectionDto();
        dto.setId(section.getId());
        dto.setTitle(section.getTitle());
        dto.setContent(section.getContent());
        dto.setDisplayOrder(section.getDisplayOrder());
        dto.setIsActive(section.getIsActive());
        dto.setCreatedAt(section.getCreatedAt());
        dto.setUpdatedAt(section.getUpdatedAt());
        return dto;
    }
    
    private SpecificObjectiveDto convertSpecificObjectiveToDto(SpecificObjective objective) {
        SpecificObjectiveDto dto = new SpecificObjectiveDto();
        dto.setId(objective.getId());
        dto.setTitle(objective.getTitle());
        dto.setDescription(objective.getDescription());
        dto.setDisplayOrder(objective.getDisplayOrder());
        dto.setIsActive(objective.getIsActive());
        return dto;
    }
}
