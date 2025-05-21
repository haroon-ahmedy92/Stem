package com.stemapplication.Service;

import com.stemapplication.DTO.AdminUserDto;
import com.stemapplication.DTO.UpdateUserDto; // Import UpdateUserDto
import com.stemapplication.DTO.UserProfileDto;

import java.util.List;

public interface AdminService {
    List<AdminUserDto> getAllAdminUsers();
    List<UserProfileDto> getAllUsers();
    AdminUserDto updateUserDetails(Long userId, UpdateUserDto updateDetails);
    void deleteUser(Long userId);
}