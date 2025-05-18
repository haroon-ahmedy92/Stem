package com.stemapplication.DTO;

import lombok.Data;

@Data
public class RegisterDto {
    private String name; // Full name
    private String username; // Login username
    private String email;
    private String password;
    private String department;
    // Role is not set by user during registration, assigned by admin or default
}