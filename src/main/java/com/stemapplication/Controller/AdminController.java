package com.stemapplication.Controller;

import com.stemapplication.Models.Admin;
import com.stemapplication.Service.AdminService;
import com.stemapplication.DTO.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AdminResponseDTO> registerAdmin(
            @Valid @RequestBody AdminCreateDTO adminDTO,
            @RequestParam String role) {

        Admin admin = new Admin();
        admin.setUsername(adminDTO.getUsername());
        admin.setPassword(adminDTO.getPassword());
        admin.setEmail(adminDTO.getEmail());

        Admin registeredAdmin = adminService.registerAdmin(admin, role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminResponseDTO(registeredAdmin));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        List<AdminResponseDTO> admins = adminService.getAllAdmins().stream()
                .map(AdminResponseDTO::new)
                .toList();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(new AdminResponseDTO(adminService.getAdminById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateDTO adminDTO) {

        Admin adminDetails = new Admin();
        adminDetails.setUsername(adminDTO.getUsername());
        adminDetails.setPassword(adminDTO.getPassword());
        adminDetails.setEmail(adminDTO.getEmail());

        return ResponseEntity.ok(
                new AdminResponseDTO(adminService.updateAdmin(id, adminDetails))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}