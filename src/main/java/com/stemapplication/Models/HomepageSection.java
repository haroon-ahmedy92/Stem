package com.stemapplication.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.stemapplication.Utils.JsonConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "homepage_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageSection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Section type is required")
    @Column(name = "section_type", nullable = false, unique = true)
    private SectionType sectionType;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(name = "title", nullable = false)
    private String title;
    
    @Size(max = 500, message = "Subtitle cannot exceed 500 characters")
    @Column(name = "subtitle", length = 500)
    private String subtitle;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "background_color", length = 50)
    private String backgroundColor;
    
    @Column(name = "content_background", length = 50)
    private String contentBackground;
    
    @Convert(converter = JsonConverter.class)
    @Column(name = "additional_data", columnDefinition = "TEXT")
    private Map<String, Object> additionalData;
    
    @Column(name = "is_published")
    private Boolean isPublished = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum SectionType {
        ACTIVITIES("activities"),
        OUTCOMES("outcomes"),
        MONITORING("monitoring"),
        ETHICS("ethics");
        
        private final String value;
        
        SectionType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static SectionType fromString(String value) {
            for (SectionType type : SectionType.values()) {
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown section type: " + value);
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
