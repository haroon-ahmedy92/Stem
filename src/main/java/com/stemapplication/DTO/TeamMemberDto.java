package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberDto {
    
    private Long id;
    private String name;
    private String qualification;
    private String role;
    private ContactDto contact;
    private String profileImage;
    private String bio;
    private String linkedin;
    private List<String> researchInterests;
    private List<Map<String, Object>> publications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
