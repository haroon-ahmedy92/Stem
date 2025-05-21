package com.stemapplication.DTO;

import lombok.Data;

// This DTO represents the request body for updating a user
@Data
public class UpdateUserDto {
    private String name;
    private String email;
    private String role; // Assuming a single role name string in the input as per spec
    private String department;
    private String status; // "Approved" or "Pending"
    // Password update should typically be a separate, more secure endpoint
}