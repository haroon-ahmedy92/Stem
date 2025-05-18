package com.stemapplication.Service;

import com.stemapplication.DTO.AuthResponseDto;
import com.stemapplication.DTO.LoginDto;
import com.stemapplication.DTO.RegisterDto;
import com.stemapplication.Models.UserEntity; // For internal use
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginDto loginDto, HttpServletResponse response);
    ResponseEntity<String> register(RegisterDto registerDto);
    ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<String> approveUser(Long userId);
    ResponseEntity<String> promoteToAdmin(Long userId);

    // Helper to create Super Admin if not exists (call once on startup)
    void createSuperAdminIfNotExists();
}