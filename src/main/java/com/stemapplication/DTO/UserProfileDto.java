package com.stemapplication.DTO;

import lombok.Data;

import java.util.List;

// Updated DTO for the GET /api/users/me response body
@Data
public class UserProfileDto {
    private Long id;
    private String name;
    private String username;
    private String email;
    private String department;
    private String status; // "Approved" or "Pending" based on approved status
    private List<String> roles; // List of role names (as per entity, clarifies spec's single string)

    // Fields added to UserEntity:
    private String phone;
    private String address;
    private String bio;
    private String birthdate; // Represented as string
    private String occupation;
    private String education;
    private String profilePictureUrl; // Represents the profilePicture URL

    // Nested DTOs corresponding to fields in UserEntity:
    private NotificationSettingsDto notifications;
    private SecuritySettingsDto security;

    private String lastLogin; // Represented as string
}