package com.stemapplication.DTO;

import lombok.Data;
import java.util.List;
// Ensure NotificationSettingsDto and SecuritySettingsDto are in this package

// DTO for the PUT /api/users/me request body
@Data
public class UpdateMyProfileDto {
    private String name;
    private String email;
    private String department;

    private String phone;
    private String address;
    private String bio;
    private String birthdate; // String in DTO, will parse to LocalDate in service
    private String occupation;
    private String education;
    private String profilePicture; // Will map to profilePictureUrl in entity

    private NotificationSettingsDto notifications; // Nested DTO
    private SecuritySettingsDto security; // Nested DTO

    // Role and Status fields from the spec are omitted as they are not typically
    // user-updatable via a /me endpoint. Admin endpoints should handle these.
}