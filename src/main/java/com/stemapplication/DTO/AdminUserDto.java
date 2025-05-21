package com.stemapplication.DTO;


import lombok.Data;

import java.util.List;

@Data
public class AdminUserDto {
    private Long id;
    private String name;
    private String email;
    private List<String> roles; // A user can have multiple roles
    private String department;
    private String status; // To represent the 'approved' status
    // private String lastLogin; // Omitting for now as it's not stored in UserEntity
}