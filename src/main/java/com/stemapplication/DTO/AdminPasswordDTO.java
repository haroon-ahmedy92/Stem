//package com.stemapplication.DTO;
//
//import jakarta.validation.constraints.*;
//
//public record AdminPasswordDTO(
//        @NotBlank(message = "Current password is required")
//        String currentPassword,
//
//        @NotBlank(message = "New password is required")
//        @Size(min = 8, max = 64, message = "Password must be 8-64 characters")
//        String newPassword
//) {}