package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTeamMemberDto {
    
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @Size(max = 500, message = "Qualification must not exceed 500 characters")
    private String qualification;
    
    @Size(max = 255, message = "Role must not exceed 255 characters")
    private String role;
    
    @Valid
    private ContactDto contact;
    
    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImage;
    
    private String bio;
    
    @Size(max = 500, message = "LinkedIn URL must not exceed 500 characters")
    private String linkedin;
    
    private List<String> researchInterests;
    
    private List<Map<String, Object>> publications;
}
