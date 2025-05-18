package com.stemapplication.Controller;

import com.stemapplication.DTO.ApproveUserDto;
import com.stemapplication.DTO.LoginDto;
import com.stemapplication.DTO.RegisterDto;
import com.stemapplication.Service.AuthService;
// Removed mappers, service layer or DTOs themselves can handle mapping if simple
// Or re-introduce them if complex mapping is needed.
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Changed base path
public class AuthController { // Renamed

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        // System.out.println("Received loginDto: " + loginDto.getUsername()); // Use getUsername()
        return authService.login(loginDto, response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()") // Or more specific role if needed
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }

    // Moved admin-specific actions here for now, can be in a separate AdminController
    @PostMapping("/admin/approve-user")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> approveUser(@RequestBody ApproveUserDto approveUserDto) {
        return authService.approveUser(approveUserDto.getUserId());
    }

    @PostMapping("/admin/promote-to-admin/{userId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> promoteToAdmin(@PathVariable Long userId) {
        return authService.promoteToAdmin(userId);
    }
}