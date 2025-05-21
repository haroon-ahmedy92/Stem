package com.stemapplication.DTO;

import lombok.Data;

import java.util.List;
// Ensure NotificationSettingsDto and SecuritySettingsDto are in this package

// DTO for the GET /api/users/me and PUT /api/users/me response body
@Data
public class MyProfileDto {
    private Long id;
    private String name;
    private String username; // Added username for completeness
    private String email;
    private String department;
    private String status; // "Approved" or "Pending" based on approved status
    private List<String> roles; // List of role names

    // Fields added to UserEntity:
    private String phone;
    private String address;
    private String bio;
    private String birthdate; // Represented as string
    private String occupation;
    private String education;
    private String profilePictureUrl; // Matches the field name in UserEntity

    // Nested DTOs corresponding to fields in UserEntity:
    private NotificationSettingsDto notifications;
    private SecuritySettingsDto security;

    private String lastLogin; // Represented as string
}