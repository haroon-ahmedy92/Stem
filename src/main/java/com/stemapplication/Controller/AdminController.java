package com.stemapplication.Controller;

import com.stemapplication.DTO.AdminUserDto;
import com.stemapplication.DTO.ApproveUserDto;
import com.stemapplication.DTO.UpdateUserDto;
import com.stemapplication.Service.AdminService;
import com.stemapplication.Service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {




    private final AdminService adminService;
    private final AuthService authService;

    @Autowired
    public AdminController(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }



    @PostMapping("/approve-user")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> approveUser(@RequestBody ApproveUserDto approveUserDto) {
        return authService.approveUser(approveUserDto.getUserId());
    }



    @PostMapping("/suspend-user")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> suspendUser(@RequestBody ApproveUserDto suspendUserDto) {
        return authService.suspendUser(suspendUserDto.getUserId());
    }


    @PostMapping("/admin/promote-to-admin/{userId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> promoteToAdmin(@PathVariable Long userId) {
        return authService.promoteToAdmin(userId);
    }




    @GetMapping("/admins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<AdminUserDto>> getAllAdmins() {
        List<AdminUserDto> adminUsers = adminService.getAllAdminUsers();
        return ResponseEntity.ok(adminUsers);
    }




    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<com.stemapplication.DTO.UserProfileDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }



    @PutMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDto updateDetails) {

        try {
            AdminUserDto updatedUser = adminService.updateUserDetails(userId, updateDetails);
            return ResponseEntity.ok(updatedUser); // Return the updated user DTO

        } catch (EntityNotFoundException e) {
            // Handle 404 Not Found for user or role
            System.err.println("Update failed: Resource not found - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (IllegalArgumentException e) {
            // Handle 400 Bad Request for invalid input (like duplicate email)
            System.err.println("Update failed: Invalid input - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("Update failed: An unexpected error occurred - " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred during user update.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }




    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {

        try {
            adminService.deleteUser(userId);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "User deleted successfully.");
            return ResponseEntity.ok(successResponse);

        } catch (EntityNotFoundException e) {
            // Handle 404 Not Found for user
            System.err.println("Delete failed: User not found - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            // Catch any other unexpected exceptions during deletion
            System.err.println("Delete failed: An unexpected error occurred - " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred during user deletion.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }




}