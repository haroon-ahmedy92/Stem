//package com.stemapplication.Controller;
//import com.stemapplication.DTO.*;
//import com.stemapplication.Service.impl.AdminService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin/profile")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminProfileController {
//
//    private final AdminService adminService;
//
//    public AdminProfileController(AdminService adminService) {
//        this.adminService = adminService;
//    }
//
//    @GetMapping
//    public ResponseEntity<AdminProfileResponseDTO> getAdminProfile() {
//        String username = getCurrentUsername();
//        return ResponseEntity.ok(adminService.getAdminProfile(username));
//    }
//
//    @PutMapping
//    public ResponseEntity<AdminProfileResponseDTO> updateProfile(
//            @Valid @RequestBody AdminProfileDTO updateDto) {
//        String username = getCurrentUsername();
//        return ResponseEntity.ok(adminService.updateAdminProfile(username, updateDto));
//    }
//
//    @PutMapping("/password")
//    public ResponseEntity<Void> changePassword(
//            @Valid @RequestBody AdminPasswordDTO passwordDto) {
//        String username = getCurrentUsername();
//        adminService.changeAdminPassword(username, passwordDto);
//        return ResponseEntity.noContent().build();
//    }
//
//    private String getCurrentUsername() {
//        return SecurityContextHolder.getContext().getAuthentication().getName();
//    }
//}