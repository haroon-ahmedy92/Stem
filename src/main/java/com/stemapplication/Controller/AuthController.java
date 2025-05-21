package com.stemapplication.Controller;

import com.stemapplication.DTO.*;
import com.stemapplication.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController { // Renamed



    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        // System.out.println("Received loginDto: " + loginDto.getUsername());
        return authService.login(loginDto, response);
    }



    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterDto registerDto) {
        return authService.register(registerDto);
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }



    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()") // Requires any authenticated user
    public ResponseEntity<?> getUserProfile(Principal principal) {
        String username = principal.getName();
        try {
            UserProfileDto userProfile = authService.getUserProfile(username);
            return ResponseEntity.ok(userProfile);
        } catch (UsernameNotFoundException e) {
            // Should not happen if isAuthenticated() passes
            System.err.println("Profile retrieval failed: User not found - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            System.err.println("Profile retrieval failed: An unexpected error occurred - " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred while retrieving profile.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @PutMapping("/users/me")
    @PreAuthorize("isAuthenticated()") // Requires any authenticated user
    public ResponseEntity<?> updateMyProfile(
            Principal principal,
            @RequestBody UpdateMyProfileDto updateDetails) {

        String username = principal.getName();

        try {
            MyProfileDto updatedProfile = authService.updateMyProfile(username, updateDetails);
            return ResponseEntity.ok(updatedProfile);

        } catch (UsernameNotFoundException e) {
            // Should not happen if isAuthenticated() passes, but handle defensively
            System.err.println("Profile update failed: Authenticated user not found - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404

        } catch (IllegalArgumentException | DateTimeParseException e) { // Handle specific bad input exceptions
            // Handle 400 Bad Request for invalid input (like duplicate email or bad date format)
            System.err.println("Profile update failed: Invalid input - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // 400

        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("Profile update failed: An unexpected error occurred - " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred during profile update.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500
        }
    }



    @PostMapping("/users/me/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> changePassword(
                                                               Principal principal,
                                                               @Valid @RequestBody ChangePasswordDto changePasswordDto) {

        String username = principal.getName();

        try {
            authService.changePassword(username, changePasswordDto);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Password updated successfully.");
            return ResponseEntity.ok(successResponse);

        } catch (UsernameNotFoundException e) {
            // Should not happen if isAuthenticated() passes
            System.err.println("Password change failed: User not found - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 Not Found

        } catch (BadCredentialsException e) {
            // Handle incorrect current password from service
            System.err.println("Password change failed: Incorrect current password - " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse); // 401 Unauthorized

        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("Password change failed: An unexpected error occurred - " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred during password change.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


}