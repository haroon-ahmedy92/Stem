package com.stemapplication.Service.impl;



import com.stemapplication.DTO.*;
import com.stemapplication.Models.RefreshToken;
import com.stemapplication.Models.Role;
import com.stemapplication.Models.UserEntity;
import com.stemapplication.Repository.RoleRepository;
import com.stemapplication.Repository.UserRepository;
import com.stemapplication.Security.JWTGenerator;
import com.stemapplication.Service.AuthService;
import com.stemapplication.Service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.jwt.refresh-cookie-name}")
    private String refreshTokenCookieName;


    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JWTGenerator jwtGenerator,
                           RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
  //@Transactional
    public ResponseEntity<?> login(LoginDto loginDto, HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
            // Authentication was successful, proceed to generate tokens, cookies, etc.

            System.out.println("Authentication successful for user: " + loginDto.getUsername()); // Added logging

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            System.out.println("Generated Token: " + token); // Added logging

            UserEntity userEntity = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication: " + authentication.getName())); // Modified message slightly
            System.out.println("User entity found for ID: " + userEntity.getId()); // Added logging


            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userEntity.getId());
            System.out.println("Refresh Token created/retrieved: " + refreshToken.getToken()); // Added logging


            ResponseCookie jwtRefreshCookie = ResponseCookie.from(refreshTokenCookieName, refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false) // Set to true in production (HTTPS)
                    .path("/api/auth/refresh-token") // Accessible only by refresh token endpoint
                    .maxAge(refreshToken.getExpiryDate().getEpochSecond() - Instant.now().getEpochSecond()) // duration in seconds
                    .sameSite("Strict") // Or "Lax" depending on your needs
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());
            System.out.println("Refresh Cookie added to response"); // Added logging


            return ResponseEntity.ok(new AuthResponseDto(token)); // Already returning a DTO (JSON)

        } catch (BadCredentialsException e) {
            System.out.println("Login failed: Invalid credentials for user: " + loginDto.getUsername()); // Added logging
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid username or password!");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

        } catch (DisabledException e) {
            // This should now primarily be caught if your CustomUserDetailsService directly throws DisabledException
            System.out.println("Login failed: Disabled account for user: " + loginDto.getUsername()); // Added logging
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User account is not approved yet or disabled.");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); // Use 403 Forbidden

        } catch (InternalAuthenticationServiceException e) { // <-- Catch the wrapper exception
            // Check if the cause is the DisabledException
            if (e.getCause() instanceof DisabledException) {
                System.out.println("Login failed: Internal service exception (Disabled account) for user: " + loginDto.getUsername()); // Added logging
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User account is not approved yet or disabled.");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); // Return 403 Forbidden

            } else {
                // If it's another type of InternalAuthenticationServiceException, re-throw or handle as internal error
                System.err.println("Login failed: Unexpected InternalAuthenticationServiceException for user: " + loginDto.getUsername()); // Added logging
                e.printStackTrace(); // Print stack trace for debugging
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "An internal authentication service error occurred.");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // Return 500
            }

        } catch (Exception e) { // <-- Catch any other unexpected exceptions during the process *after* authenticate()
            System.err.println("Login failed: An unexpected error occurred after authentication for user: " + loginDto.getUsername()); // Added logging
            e.printStackTrace(); // Print stack trace for debugging
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred during login.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // Return 500
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Username is already taken!");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST
            );
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Email is already in use!");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setDepartment(registerDto.getDepartment());
        user.setApproved(false); // New users are not approved by default

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role newUserRole = new Role();
                    newUserRole.setName("ROLE_USER");
                    return roleRepository.save(newUserRole);
                });
        user.setRoles(Collections.singletonList(defaultRole));

        userRepository.save(user);

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "User registered successfully! Awaiting approval.");
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
// @Transactional // Refresh token process might involve DB ops (delete/create token)
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String providedRefreshToken = null;
        if (request.getCookies() != null) {
            providedRefreshToken = Stream.of(request.getCookies())
                    .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (providedRefreshToken == null) {
            System.out.println("Refresh token not found in cookie."); // Add logging
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Refresh token not found in cookie.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            // Find the token in the database
            Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(providedRefreshToken);

            if (!refreshTokenOptional.isPresent()) {
                System.out.println("Invalid refresh token: Token not found in DB for token: " + providedRefreshToken); // Add logging
                // Optionally delete a potentially malformed cookie if it exists but token isn't in DB
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                        .maxAge(0)
                        .path("/api/auth/refresh-token")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict").build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid refresh token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            RefreshToken refreshToken = refreshTokenOptional.get();

            // Verify token expiration (this method throws RuntimeException if expired)
            refreshTokenService.verifyExpiration(refreshToken); // This updates the token entity potentially, or just validates


            // Get the user associated with the refresh token
            UserEntity userEntity = refreshToken.getUser();
            if (userEntity == null) {
                System.err.println("Refresh token found but user association is null for token: " + providedRefreshToken); // Add logging
                // Invalid state, delete the refresh token
                refreshTokenService.deleteByToken(providedRefreshToken);
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                        .maxAge(0)
                        .path("/api/auth/refresh-token")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict").build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid token: User not associated.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // Check if the user account is approved/enabled
            if (!userEntity.isApproved()) { // Assuming isApproved() is the check you need
                System.out.println("Refresh token valid but user account not approved: " + userEntity.getUsername()); // Add logging
                // Optionally delete refresh token for disabled user
                refreshTokenService.deleteByToken(providedRefreshToken);
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                        .maxAge(0)
                        .path("/api/auth/refresh-token")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict")
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User account is not approved yet or disabled.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }


            // If token is valid and user is active, create a new access token
            // Create an Authentication object based on the user from the refresh token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userEntity.getUsername(), // principal (username)
                    null, // credentials (not needed for token-based auth after initial login)
                    userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()) // user authorities/roles
            );
            SecurityContextHolder.getContext().setAuthentication(authentication); // Set authentication context

            // Generate a new JWT access token
            String newAccessToken = jwtGenerator.generateToken(authentication);
            System.out.println("New Access Token generated via refresh for user: " + userEntity.getUsername()); // Add logging


            // *** Refresh Token Rotation (Optional but Recommended) ***
            // Delete the old refresh token before creating a new one
            refreshTokenService.deleteByToken(providedRefreshToken);
            // Create and save a new refresh token
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userEntity.getId());

            // Set the new refresh token in an HttpOnly cookie
            ResponseCookie jwtRefreshCookie = ResponseCookie.from(refreshTokenCookieName, newRefreshToken.getToken())
                    .httpOnly(true)
                    .secure(false) // Use true in production with HTTPS
                    .path("/api/auth/refresh-token") // Set path appropriately
                    .maxAge(newRefreshToken.getExpiryDate().getEpochSecond() - Instant.now().getEpochSecond())
                    .sameSite("Strict") // Choose Strict or Lax based on needs
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());
            System.out.println("New Refresh Cookie set for user: " + userEntity.getUsername()); // Add logging


            // Return the new access token in the body
            return ResponseEntity.ok(new AuthResponseDto(newAccessToken)); // Already returning a DTO (JSON)

        } catch (ExpiredJwtException e) { // Catch JJWT specific expired exception if verifyExpiration throws it
            System.out.println("Refresh token expired for token: " + providedRefreshToken); // Add logging
            // Clean up expired token from DB if verifyExpiration didn't already
            refreshTokenService.deleteByToken(providedRefreshToken); // Ensure it's deleted
            ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).path("/api/auth/refresh-token").httpOnly(true).secure(false).sameSite("Strict").build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Refresh token expired. Please log in again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (RuntimeException e) { // Catch RuntimeExceptions from verifyExpiration (if it throws generic) or other issues
            System.err.println("Runtime error during refresh token process for token: " + providedRefreshToken + " - " + e.getMessage()); // Add logging
            e.printStackTrace(); // Print stack trace
            // Decide how to handle - often treated as invalid token or internal error
            ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                    .maxAge(0)
                    .path("/api/auth/refresh-token")
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid refresh token."); // Or INTERNAL_SERVER_ERROR depending on expected cause
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) { // Catch any other unexpected exceptions
            System.err.println("An unexpected error occurred during refresh token process for token: " + providedRefreshToken); // Add logging
            e.printStackTrace();
            ResponseCookie expiredCookie = ResponseCookie
                    .from(refreshTokenCookieName, "")
                    .maxAge(0)
                    .path("/api/auth/refresh-token")
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred during token refresh.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        String providedRefreshToken = null;
        if (request.getCookies() != null) {
            providedRefreshToken = Stream.of(request.getCookies())
                    .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        System.out.println("Logout attempting to delete token: >>" + providedRefreshToken + "<<"); // Add this line

        if (providedRefreshToken != null) {
            refreshTokenService.deleteByToken(providedRefreshToken);
        }

        ResponseCookie refreshCookie = ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(true)
                .secure(true) // Set to true in production
                .path("/api/auth/refresh-token")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        SecurityContextHolder.clearContext(); // Clear security context

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Logged out successfully.");
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }


    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> approveUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId)); // Use EntityNotFoundException for 404

        user.setApproved(true);
        userRepository.save(user);

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "User " + user.getUsername() + " approved successfully.");
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }


    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> suspendUser(Long userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found with ID: " + userId));

            // Set user's approval status to false
            user.setApproved(false);
            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User has been suspended successfully");
            return ResponseEntity.ok(response);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred while suspending the user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> promoteToAdmin(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found. Please create it."));

        // Avoid duplicate roles
        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            user.getRoles().add(adminRole);
            // If user was only ROLE_USER and now becomes ROLE_ADMIN, you might want to remove ROLE_USER
            // or keep both depending on your privilege system. For simplicity, we add.
            // If they were pending and only had ROLE_USER, ensure they are approved too.
            if (!user.isApproved()) {
                user.setApproved(true);
            }
            userRepository.save(user);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "User " + user.getUsername() + " promoted to Admin successfully.");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User " + user.getUsername() + " is already an Admin.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public UserProfileDto getUserProfile(String username) {
        // Find the user by username
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)); // Throw standard exception if not found

        // Map UserEntity to UserProfileDto
        UserProfileDto dto = new UserProfileDto();

        // Basic User Fields
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDepartment(user.getDepartment());
        dto.setStatus(user.isApproved() ? "Approved" : "Pending"); // Map approved boolean to string status
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList())); // Map roles to list of strings

        // Additional Profile Fields (Added to UserEntity)
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setBio(user.getBio());
        // Map LocalDate/LocalDateTime to String
        dto.setBirthdate(user.getBirthdate() != null ? user.getBirthdate().toString() : null); // Simple toString()
        dto.setOccupation(user.getOccupation());
        dto.setEducation(user.getEducation());
        dto.setProfilePictureUrl(user.getProfilePictureUrl()); // Use the field name from UserEntity

        // Nested Notification Settings
        NotificationSettingsDto notifications = new NotificationSettingsDto();
        notifications.setEmail(user.isNotificationEmailEnabled());
        notifications.setApp(user.isNotificationAppEnabled());
        notifications.setUpdates(user.isNotificationUpdatesEnabled());
        dto.setNotifications(notifications);

        // Nested Security Settings
        SecuritySettingsDto security = new SecuritySettingsDto();
        security.setTwoFactor(user.isSecurityTwoFactorEnabled());
        security.setSessionTimeout(user.getSecuritySessionTimeout()); // Handles null if column is nullable
        dto.setSecurity(security);

        // Last Login Field
        // Map LocalDateTime to String
        dto.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().toString() : null); // Simple toString()


        return dto;
    }












    @Override
    @Transactional // Add Transactional annotation as this modifies the database
    public MyProfileDto updateMyProfile(String username, UpdateMyProfileDto updateDetails) {
        // 1. Find the user by username (authenticated user)
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + username));
        // This exception is unlikely if called after successful authentication, but good practice.

        // 2. Update fields from the DTO where the DTO field is not null

        // Basic Fields
        if (updateDetails.getName() != null) {
            user.setName(updateDetails.getName());
        }
        if (updateDetails.getEmail() != null) {
            user.setEmail(updateDetails.getEmail());
        }
        if (updateDetails.getDepartment() != null) {
            user.setDepartment(updateDetails.getDepartment());
        }

        // Additional Profile Fields
        if (updateDetails.getPhone() != null) {
            user.setPhone(updateDetails.getPhone());
        }
        if (updateDetails.getAddress() != null) {
            user.setAddress(updateDetails.getAddress());
        }
        if (updateDetails.getBio() != null) {
            user.setBio(updateDetails.getBio());
        }
        if (updateDetails.getBirthdate() != null && !updateDetails.getBirthdate().isEmpty()) {
            try {
                // Assuming birthdate string is in a parseable format like "YYYY-MM-DD"
                user.setBirthdate(LocalDate.parse(updateDetails.getBirthdate()));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid birthdate format. Use YYYY-MM-DD.", e); // 400 Bad Request
            }
        }
        if (updateDetails.getOccupation() != null) {
            user.setOccupation(updateDetails.getOccupation());
        }
        if (updateDetails.getEducation() != null) {
            user.setEducation(updateDetails.getEducation());
        }
        if (updateDetails.getProfilePicture() != null) {
            user.setProfilePictureUrl(updateDetails.getProfilePicture()); // Map profilePicture in DTO to profilePictureUrl in Entity
        }

        // Nested Notification Settings (Update individual boolean fields)
        if (updateDetails.getNotifications() != null) {
            NotificationSettingsDto notificationsDto = updateDetails.getNotifications();
            user.setNotificationEmailEnabled(notificationsDto.isEmail());
            user.setNotificationAppEnabled(notificationsDto.isApp());
            user.setNotificationUpdatesEnabled(notificationsDto.isUpdates());
        }

        // Nested Security Settings (Update individual fields)
        if (updateDetails.getSecurity() != null) {
            SecuritySettingsDto securityDto = updateDetails.getSecurity();
            user.setSecurityTwoFactorEnabled(securityDto.isTwoFactor());
            user.setSecuritySessionTimeout(securityDto.getSessionTimeout()); // Handles null automatically
        }

        // --- Note: Ignoring fields not intended for user self-update ---
        // Fields like 'role' and 'status' (approved) from the spec are ignored here.


        // 3. Save the updated user
        try {
            UserEntity updatedUser = userRepository.save(user);

            // 4. Map the updated user back to MyProfileDto for the response
            MyProfileDto dto = new MyProfileDto();
            dto.setId(updatedUser.getId());
            dto.setName(updatedUser.getName());
            dto.setUsername(updatedUser.getUsername());
            dto.setEmail(updatedUser.getEmail());
            dto.setDepartment(updatedUser.getDepartment());
            dto.setStatus(updatedUser.isApproved() ? "Approved" : "Pending");
            dto.setRoles(updatedUser.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList()));

            // Additional Profile Fields (Map from updated entity)
            dto.setPhone(updatedUser.getPhone());
            dto.setAddress(updatedUser.getAddress());
            dto.setBio(updatedUser.getBio());
            dto.setBirthdate(updatedUser.getBirthdate() != null ? updatedUser.getBirthdate().toString() : null); // Map LocalDate to String
            dto.setOccupation(updatedUser.getOccupation());
            dto.setEducation(updatedUser.getEducation());
            dto.setProfilePictureUrl(updatedUser.getProfilePictureUrl());

            // Nested Notification Settings (Map from individual entity fields)
            NotificationSettingsDto notifications = new NotificationSettingsDto();
            notifications.setEmail(updatedUser.isNotificationEmailEnabled());
            notifications.setApp(updatedUser.isNotificationAppEnabled());
            notifications.setUpdates(updatedUser.isNotificationUpdatesEnabled());
            dto.setNotifications(notifications);

            // Nested Security Settings (Map from individual entity fields)
            SecuritySettingsDto security = new SecuritySettingsDto();
            security.setTwoFactor(updatedUser.isSecurityTwoFactorEnabled());
            security.setSessionTimeout(updatedUser.getSecuritySessionTimeout());
            dto.setSecurity(security);

            // Last Login Field (Map from entity)
            dto.setLastLogin(updatedUser.getLastLogin() != null ? updatedUser.getLastLogin().toString() : null); // Map LocalDateTime to String

            return dto;

        } catch (DataIntegrityViolationException e) {
            // Catch unique constraint violations (e.g., duplicate email)
            throw new IllegalArgumentException("Data integrity violation: Email already in use or invalid data.", e); // Throw IllegalArgumentException for 400
        }
        // Catch other potential exceptions here if needed
    }





    @Override
    @Transactional // Add Transactional as this modifies the database
    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        // 1. Find the user by username (authenticated user)
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + username));
        // Unlikely if called after successful authentication

        // 2. Verify the current password
        // passwordEncoder.matches(rawPassword, encodedPassword)
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            // If current password doesn't match, throw an exception
            throw new BadCredentialsException("Incorrect current password"); // Throw Spring Security's BadCredentialsException
        }

        // 3. The DTO validation (@PasswordMatches) ensures newPassword and confirmPassword match

        // 4. Encode the new password
        String encodedNewPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());

        // 5. Set the new encoded password on the user entity
        user.setPassword(encodedNewPassword);

        // 6. Save the updated user
        userRepository.save(user); // JPA will update the existing user

        System.out.println("Password successfully changed for user: " + username); // Logging
    }






    @Override
    @Transactional // Important for DB operations
    public void createSuperAdminIfNotExists() {
        final String superAdminUsername = "superadmin";
        if (!userRepository.existsByUsername(superAdminUsername)) {
            UserEntity superAdmin = new UserEntity();
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setName("Super Administrator");
            superAdmin.setEmail("superadmin@example.com"); // Change as needed
            superAdmin.setPassword(passwordEncoder.encode("superadmin123")); // Change in production
            superAdmin.setApproved(true);
            superAdmin.setDepartment("SYSTEM");

            Role superAdminRole = roleRepository.findByName("ROLE_SUPER_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_SUPER_ADMIN");
                        return roleRepository.save(newRole);
                    });
            // Ensure ROLE_ADMIN and ROLE_USER also exist for general functionality
            roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_ADMIN");
                        return roleRepository.save(newRole);
                    });
            roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });


            superAdmin.setRoles(Collections.singletonList(superAdminRole));
            userRepository.save(superAdmin);
            System.out.println("Super Admin account created with username: " + superAdminUsername);
        }
    }
}