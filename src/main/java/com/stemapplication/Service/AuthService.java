package com.stemapplication.Service;

import com.stemapplication.DTO.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthService {

    ResponseEntity<?> login(LoginDto loginDto, HttpServletResponse response);
    ResponseEntity<Map<String, String>> register(RegisterDto registerDto);
    ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<Map<String,String>> approveUser(Long userId);
    ResponseEntity<Map<String, String>> promoteToAdmin(Long userId);
    UserProfileDto getUserProfile(String username);
    MyProfileDto updateMyProfile(String username, UpdateMyProfileDto updateDetails);
    void changePassword(String username, ChangePasswordDto changePasswordDto);
    void createSuperAdminIfNotExists();

}