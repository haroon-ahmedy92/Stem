package com.stemapplication.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_members")
public class TeamMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false, length = 500)
    private String qualification;
    
    @Column(nullable = false, length = 255)
    private String role;
    
    // Contact information - stored as separate columns for better querying
    @Column(length = 500)
    private String address;
    
    @Column(length = 255)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "profile_image", length = 500)
    private String profileImage;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(length = 500)
    private String linkedin;
    
    // Store as JSON string - will be converted in DTOs
    @Column(name = "research_interests", columnDefinition = "JSON")
    private String researchInterests;
    
    // Store as JSON string - will be converted in DTOs
    @Column(columnDefinition = "JSON")
    private String publications;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
