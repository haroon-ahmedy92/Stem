package com.stemapplication.DTO;

import com.stemapplication.Models.Admin;

import java.time.LocalDate;

public record AdminProfileResponseDTO(
        String fullName,
        String phoneNumber,
        LocalDate dateOfBirth,
        String occupation,
        String bio,
        String email,
        String address,
        String role,
        String education,
        String department
) {
    public AdminProfileResponseDTO(Admin admin) {
        this(
                admin.getFullName(),
                admin.getPhoneNumber(),
                admin.getDateOfBirth(),
                admin.getOccupation(),
                admin.getBio(),
                admin.getEmail(),
                admin.getAddress(),
                admin.getRoles().iterator().next(),
                admin.getEducation(),
                admin.getDepartment()
        );
    }
}