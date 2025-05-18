package com.stemapplication.DTO;
import com.stemapplication.Models.Admin;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;
public record AdminResponseDTO(
        Long id,
        String username,
        String email,
        Set<String> roles,
        String fullName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String occupation,
        String bio,
        String address,
        String education,
        String department,
        boolean enabled
) {
    public AdminResponseDTO(Admin admin) {
        this(
                admin.getId(),
                admin.getUsername(),
                admin.getEmail(),
                admin.getRoles(),
                admin.getFullName(),
                admin.getPhoneNumber(),
                admin.getDateOfBirth(),
                admin.getOccupation(),
                admin.getBio(),
                admin.getAddress(),
                admin.getEducation(),
                admin.getDepartment(),
                admin.isEnabled()
        );
    }
}