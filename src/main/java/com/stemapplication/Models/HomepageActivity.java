package com.stemapplication.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.stemapplication.Utils.JsonConverter;
import com.stemapplication.Utils.JsonListConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "homepage_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepageActivity {
    
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
    
    @Column(name = "color", length = 50)
    private String color;
    
    @Column(name = "order_index")
    private Integer orderIndex = 0;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "is_published")
    private Boolean isPublished = true;
    
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;
    
    @Column(name = "link", length = 500)
    private String link;
    
    @Column(name = "image", length = 500)
    private String image;
    
    @Convert(converter = JsonListConverter.class)
    @Column(name = "tags", columnDefinition = "TEXT")
    private List<String> tags;
    
    @Convert(converter = JsonConverter.class)
    @Column(name = "progress", columnDefinition = "TEXT")
    private Map<String, Object> progress;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
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
