package com.stemapplication.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.stemapplication.Utils.JsonConverter;
import com.stemapplication.Utils.JsonListMapConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "homepage_outcomes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageOutcome {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(name = "title", nullable = false)
    private String title;
    
    @NotNull(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "icon_class", length = 100)
    private String iconClass;
    
    @Column(name = "order_index")
    private Integer orderIndex = 0;
    
    @Column(name = "is_published")
    private Boolean isPublished = true;
    
    @Convert(converter = JsonConverter.class)
    @Column(name = "metrics", columnDefinition = "TEXT")
    private Map<String, Object> metrics;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OutcomeStatus status = OutcomeStatus.ON_TRACK;
    
    @Convert(converter = JsonListMapConverter.class)
    @Column(name = "milestones", columnDefinition = "TEXT")
    private List<Map<String, Object>> milestones;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum OutcomeStatus {
        ON_TRACK("on-track"),
        BEHIND("behind"),
        COMPLETED("completed"),
        AT_RISK("at-risk");
        
        private final String value;
        
        OutcomeStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
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
