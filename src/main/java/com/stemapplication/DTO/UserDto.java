package com.stemapplication.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String name; // Display name
    private String email;
    private String profilePictureUrl;
    // Do NOT include sensitive fields like password, roles, etc.
}