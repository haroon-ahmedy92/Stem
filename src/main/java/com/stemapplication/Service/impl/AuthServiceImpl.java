package com.stemapplication.Service.impl;

import com.stemapplication.DTO.*;
import com.stemapplication.Models.RefreshToken;
import com.stemapplication.Models.Role;
import com.stemapplication.Models.UserEntity;
import com.stemapplication.Repository.RoleRepository;
import com.stemapplication.Repository.UserRepository;
import com.stemapplication.Security.JWTGenerator;
import com.stemapplication.Service.ActivityLogService;
import com.stemapplication.Service.AuthService;
import com.stemapplication.Service.RefreshTokenService;
import com.stemapplication.Utils.ActivityLogger;
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

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the AuthService interface with activity logging integration.
 * This service handles user authentication, registration, profile management,
 * token operations, and account management.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;
    private final RefreshTokenService refreshTokenService;
    private final ActivityLogService activityLogService;
    private final ActivityLogger activityLogger;

    @Value("${app.jwt.refresh-cookie-name}")
    private String refreshTokenCookieName;

    public AuthServiceImpl(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder,
                                   AuthenticationManager authenticationManager,
                                   JWTGenerator jwtGenerator,
                                   RefreshTokenService refreshTokenService,
                                   ActivityLogService activityLogService,
                                   ActivityLogger activityLogger) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.refreshTokenService = refreshTokenService;
        this.activityLogService = activityLogService;
        this.activityLogger = activityLogger;
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
            System.out.println("Authentication successful for user: " + loginDto.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);

            UserEntity userEntity = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication: " + authentication.getName()));

            // Log successful login
            activityLogger.logLogin(userEntity.getUsername(), userEntity.getId());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userEntity.getId());

            ResponseCookie jwtRefreshCookie = ResponseCookie.from(refreshTokenCookieName, refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false) // Set to true in production (HTTPS)
                    .path("/api/auth/refresh-token") // Accessible only by refresh token endpoint
                    .maxAge(refreshToken.getExpiryDate().getEpochSecond() - Instant.now().getEpochSecond()) // duration in seconds
                    .sameSite("Strict") // Or "Lax" depending on your needs
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

            return ResponseEntity.ok(new AuthResponseDto(token));

        } catch (BadCredentialsException e) {
            // Log failed login attempt
            activityLogger.logFailedLogin(loginDto.getUsername(), "Invalid credentials");

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid username or password!");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

        } catch (DisabledException e) {
            // Log disabled account attempt
            activityLogger.logFailedLogin(loginDto.getUsername(), "Account disabled or not approved");

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User account is not approved yet or disabled.");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);

        } catch (InternalAuthenticationServiceException e) {
            if (e.getCause() instanceof DisabledException) {
                // Log disabled account attempt
                activityLogger.logFailedLogin(loginDto.getUsername(), "Account disabled or not approved");

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User account is not approved yet or disabled.");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            } else {
                // Log system error
                activityLogger.logSystemError("Authentication service error for user: " + loginDto.getUsername());

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "An internal authentication service error occurred.");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            // Log unexpected error
            activityLogger.logSystemError("Unexpected error during login for user: " + loginDto.getUsername());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred during login.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @Override
//    @Transactional  // ✅ ADD @Transactional for consistency
//    public ResponseEntity<?> login(LoginDto loginDto, HttpServletResponse response) {
//        Authentication authentication;
//        try {
//            authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
//            );
//            // Authentication was successful, proceed to generate tokens, cookies, etc.
//            System.out.println("Authentication successful for user: " + loginDto.getUsername());
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            String token = jwtGenerator.generateToken(authentication);
//
//            UserEntity userEntity = userRepository.findByUsername(authentication.getName())
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication: " + authentication.getName()));
//
//            // Log successful login
//            activityLogger.logLogin(userEntity.getUsername(), userEntity.getId());
//
//            // ✅ Clear any existing refresh tokens for this user before creating new one
//            refreshTokenService.deleteByUserId(userEntity.getId());
//
//            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userEntity.getId());
//
//            // ✅ USE CONSISTENT COOKIE SETTINGS WITH HELPER METHOD
//            setRefreshTokenCookie(response, refreshToken);
//
//            return ResponseEntity.ok(new AuthResponseDto(token));
//
//        } catch (BadCredentialsException e) {
//            // Log failed login attempt
//            activityLogger.logFailedLogin(loginDto.getUsername(), "Invalid credentials");
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Invalid username or password!");
//            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
//
//        } catch (DisabledException e) {
//            // Log disabled account attempt
//            activityLogger.logFailedLogin(loginDto.getUsername(), "Account disabled or not approved");
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "User account is not approved yet or disabled.");
//            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
//
//        } catch (InternalAuthenticationServiceException e) {
//            if (e.getCause() instanceof DisabledException) {
//                // Log disabled account attempt
//                activityLogger.logFailedLogin(loginDto.getUsername(), "Account disabled or not approved");
//
//                Map<String, String> errorResponse = new HashMap<>();
//                errorResponse.put("message", "User account is not approved yet or disabled.");
//                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
//            } else {
//                // Log system error
//                activityLogger.logSystemError("Authentication service error for user: " + loginDto.getUsername());
//
//                Map<String, String> errorResponse = new HashMap<>();
//                errorResponse.put("message", "An internal authentication service error occurred.");
//                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        } catch (Exception e) {
//            // Log unexpected error
//            activityLogger.logSystemError("Unexpected error during login for user: " + loginDto.getUsername());
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "An unexpected error occurred during login.");
//            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }



    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Username is already taken!");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

        // Log user registration
        activityLogger.logRegistration(user.getUsername());

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "User registered successfully! Awaiting approval.");
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }







    @Override
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
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Refresh token not found in cookie.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            // Find the token in the database
            Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(providedRefreshToken);

            if (!refreshTokenOptional.isPresent()) {
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                        .maxAge(0)
                        .path("/api/auth/refresh-token")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict").build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

                // Log invalid token attempt
                activityLogger.logSystemActivity("Invalid refresh token attempt");

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid refresh token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            RefreshToken refreshToken = refreshTokenOptional.get();

            // Verify token expiration
            refreshTokenService.verifyExpiration(refreshToken);

            // Get the user associated with the refresh token
            UserEntity userEntity = refreshToken.getUser();
            if (userEntity == null) {
                refreshTokenService.deleteByToken(providedRefreshToken);
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                        .maxAge(0)
                        .path("/api/auth/refresh-token")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict").build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

                // Log invalid token attempt
                activityLogger.logSystemActivity("Invalid token: User not associated");

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Invalid token: User not associated.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // Check if the user account is approved/enabled
            if (!userEntity.isApproved()) {
                refreshTokenService.deleteByToken(providedRefreshToken);
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                        .maxAge(0)
                        .path("/api/auth/refresh-token")
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict")
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

                // Log disabled account attempt
                activityLogger.logSystemActivity("Refresh token attempt on disabled account: " + userEntity.getUsername());

                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "User account is not approved yet or disabled.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            // If token is valid and user is active, create a new access token
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userEntity.getUsername(),
                    null,
                    userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate a new JWT access token
            String newAccessToken = jwtGenerator.generateToken(authentication);

            // Delete the old refresh token before creating a new one
            refreshTokenService.deleteByToken(providedRefreshToken);
            // Create and save a new refresh token
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userEntity.getId());

            // Set the new refresh token in an HttpOnly cookie
            ResponseCookie jwtRefreshCookie = ResponseCookie.from(refreshTokenCookieName, newRefreshToken.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/api/auth/refresh-token")
                    .maxAge(newRefreshToken.getExpiryDate().getEpochSecond() - Instant.now().getEpochSecond())
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());

            // Log successful token refresh
            activityLogger.logSystemActivity("Token refreshed for user: " + userEntity.getUsername());

            // Return the new access token in the body
            return ResponseEntity.ok(new AuthResponseDto(newAccessToken));

        } catch (ExpiredJwtException e) {
            refreshTokenService.deleteByToken(providedRefreshToken);
            ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).path("/api/auth/refresh-token").httpOnly(true).secure(false).sameSite("Strict").build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

            // Log expired token
            activityLogger.logSystemActivity("Refresh token expired");

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Refresh token expired. Please log in again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (RuntimeException e) {
            ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                    .maxAge(0)
                    .path("/api/auth/refresh-token")
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

            // Log error
            activityLogger.logSystemError("Runtime error during refresh token process: " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid refresh token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            ResponseCookie expiredCookie = ResponseCookie
                    .from(refreshTokenCookieName, "")
                    .maxAge(0)
                    .path("/api/auth/refresh-token")
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

            // Log unexpected error
            activityLogger.logSystemError("Unexpected error during token refresh: " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred during token refresh.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



//    @Override
//    @Transactional  // ✅ ADD THIS!
//    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
//        String providedRefreshToken = null;
//        if (request.getCookies() != null) {
//            providedRefreshToken = Stream.of(request.getCookies())
//                    .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
//                    .map(Cookie::getValue)
//                    .findFirst()
//                    .orElse(null);
//        }
//
//        if (providedRefreshToken == null) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Refresh token not found in cookie.");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//        }
//
//        try {
//            // Find the token in the database
//            Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(providedRefreshToken);
//
//            if (!refreshTokenOptional.isPresent()) {
//                clearRefreshTokenCookie(response);
//                Map<String, String> errorResponse = new HashMap<>();
//                errorResponse.put("message", "Invalid refresh token.");
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//            }
//
//            RefreshToken refreshToken = refreshTokenOptional.get();
//
//            // Verify token expiration
//            refreshTokenService.verifyExpiration(refreshToken);
//
//            // Get the user associated with the refresh token
//            UserEntity userEntity = refreshToken.getUser();
//            if (userEntity == null || !userEntity.isApproved()) {
//                refreshTokenService.deleteByToken(providedRefreshToken);
//                clearRefreshTokenCookie(response);
//                Map<String, String> errorResponse = new HashMap<>();
//                errorResponse.put("message", "User account is not approved or invalid.");
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
//            }
//
//            // Create authentication for new JWT
//            Authentication authentication = new UsernamePasswordAuthenticationToken(
//                    userEntity.getUsername(),
//                    null,
//                    userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // Generate a new JWT access token
//            String newAccessToken = jwtGenerator.generateToken(authentication);
//
//            // ✅ ATOMIC TOKEN ROTATION - Create new token with automatic old token deletion
//            RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(providedRefreshToken, userEntity.getId());
//
//            // Set the new refresh token in cookie
//            setRefreshTokenCookie(response, newRefreshToken);
//
//            activityLogger.logSystemActivity("Token refreshed for user: " + userEntity.getUsername());
//
//            return ResponseEntity.ok(new AuthResponseDto(newAccessToken));
//
//        } catch (RuntimeException e) {
//            clearRefreshTokenCookie(response);
//            activityLogger.logSystemError("Runtime error during refresh token process: " + e.getMessage());
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Invalid refresh token.");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//        } catch (Exception e) {
//            clearRefreshTokenCookie(response);
//            activityLogger.logSystemError("Unexpected error during token refresh: " + e.getMessage());
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "An unexpected error occurred during token refresh.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }






    // ✅ HELPER METHODS FOR CONSISTENT COOKIE HANDLING
    private void setRefreshTokenCookie(HttpServletResponse response, RefreshToken refreshToken) {
        ResponseCookie jwtRefreshCookie = ResponseCookie.from(refreshTokenCookieName, refreshToken.getToken())
                .httpOnly(true)
                .secure(false) // Set consistently based on environment
                .path("/api/auth") // ✅ BROADER PATH for consistency
                .maxAge(refreshToken.getExpiryDate().getEpochSecond() - Instant.now().getEpochSecond())
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "")
                .maxAge(0)
                .path("/api/auth") // ✅ SAME PATH as set cookie
                .httpOnly(true)
                .secure(false) // ✅ CONSISTENT with set cookie
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
    }







    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        // Get the current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";

        String providedRefreshToken = null;
        if (request.getCookies() != null) {
            providedRefreshToken = Stream.of(request.getCookies())
                    .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (providedRefreshToken != null) {
            refreshTokenService.deleteByToken(providedRefreshToken);
        }

        ResponseCookie refreshCookie = ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh-token")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        SecurityContextHolder.clearContext();

        // Log logout
        if (!username.equals("anonymous")) {
            activityLogger.logLogout(username);
        }

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Logged out successfully.");
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }


//
//    @Override
//    @Transactional
//    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth != null ? auth.getName() : "anonymous";
//
//        String providedRefreshToken = null;
//        if (request.getCookies() != null) {
//            providedRefreshToken = Stream.of(request.getCookies())
//                    .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
//                    .map(Cookie::getValue)
//                    .findFirst()
//                    .orElse(null);
//        }
//
//        if (providedRefreshToken != null) {
//            refreshTokenService.deleteByToken(providedRefreshToken);
//        }
//
//        // ✅ USE CONSISTENT COOKIE CLEARING
//        clearRefreshTokenCookie(response);
//
//        SecurityContextHolder.clearContext();
//
//        if (!username.equals("anonymous")) {
//            activityLogger.logLogout(username);
//        }
//
//        Map<String, String> successResponse = new HashMap<>();
//        successResponse.put("message", "Logged out successfully.");
//        return new ResponseEntity<>(successResponse, HttpStatus.OK);
//    }








    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> approveUser(Long userId) {
        // Get the current authenticated user (the admin performing the approval)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = auth != null ? auth.getName() : "SYSTEM";

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        user.setApproved(true);
        userRepository.save(user);

        // Log user approval
        activityLogger.logUserApproval(adminUsername, user.getUsername(), user.getId());

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "User " + user.getUsername() + " approved successfully.");
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> suspendUser(Long userId) {
        // Get the current authenticated user (the admin performing the suspension)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = auth != null ? auth.getName() : "SYSTEM";

        try {
            UserEntity userToSuspend = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

            // Get the admin user entity
            UserEntity adminUser = userRepository.findByUsername(adminUsername)
                    .orElseThrow(() -> new EntityNotFoundException("Admin user not found: " + adminUsername));

            // Check if trying to suspend self
            if (adminUser.getId().equals(userId)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "You cannot suspend your own account.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            // Check permissions based on roles
            boolean isAdminRole = adminUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN") && !role.getName().equals("ROLE_SUPER_ADMIN"));

            boolean isSuperAdmin = adminUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_SUPER_ADMIN"));

            // Check if target user is an admin
            boolean targetIsAdmin = userToSuspend.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_SUPER_ADMIN"));

            // Regular admin trying to suspend another admin
            if (isAdminRole && targetIsAdmin) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Regular admins cannot suspend other administrators.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            // Suspend the user
            userToSuspend.setApproved(false);
            userRepository.save(userToSuspend);

            // Log user suspension
            activityLogger.logUserSuspension(adminUsername, userToSuspend.getUsername(), userToSuspend.getId());

            Map<String, String> response = new HashMap<>();
            response.put("message", "User has been suspended successfully");
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (Exception e) {
            // Log error
            activityLogger.logSystemError("Error suspending user ID " + userId + ": " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An error occurred while suspending the user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> promoteToAdmin(Long userId) {
        // Get the current authenticated user (the super admin performing the promotion)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = auth != null ? auth.getName() : "SYSTEM";

        // Get the currently authenticated user's details
        UserEntity currentUser = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new EntityNotFoundException("Current authenticated user not found"));

        // Check if super admin is trying to promote themselves
        if (currentUser.getId().equals(userId)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "You cannot promote yourself to Admin as you already have Super Admin privileges.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found. Please create it."));

        // Avoid duplicate roles
        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            user.getRoles().add(adminRole);
            if (!user.isApproved()) {
                user.setApproved(true);
            }
            userRepository.save(user);

            // Log role promotion
            activityLogger.logRolePromotion(adminUsername, user.getUsername(), user.getId(), "ADMIN");

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
    @Transactional
    public ResponseEntity<Map<String, String>> demoteFromAdmin(Long userId) {
        // Get the current authenticated user (the super admin performing the demotion)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = auth != null ? auth.getName() : "SYSTEM";

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found. Please create it."));

        // Check if user has the admin role
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            // Remove the admin role
            user.getRoles().removeIf(role -> role.getName().equals("ROLE_ADMIN"));

            // Ensure the user still has at least ROLE_USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalStateException("ROLE_USER not found. Please create it."));

            if (user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_USER"))) {
                user.getRoles().add(userRole);
            }

            userRepository.save(user);

            // Log role demotion
            activityLogger.logRoleDemotion(adminUsername, user.getUsername(), user.getId());

            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "User " + user.getUsername() + " has been demoted from Admin role.");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User " + user.getUsername() + " is not an Admin.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public UserProfileDto getUserProfile(String username) {
        // Find the user by username
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Map UserEntity to UserProfileDto
        UserProfileDto dto = new UserProfileDto();

        // Basic User Fields
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDepartment(user.getDepartment());
        dto.setStatus(user.isApproved() ? "Approved" : "Pending");
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList()));

        // Additional Profile Fields
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setBio(user.getBio());
        dto.setBirthdate(user.getBirthdate() != null ? user.getBirthdate().toString() : null);
        dto.setOccupation(user.getOccupation());
        dto.setEducation(user.getEducation());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());

        // Nested Notification Settings
        NotificationSettingsDto notifications = new NotificationSettingsDto();
        notifications.setEmail(user.isNotificationEmailEnabled());
        notifications.setApp(user.isNotificationAppEnabled());
        notifications.setUpdates(user.isNotificationUpdatesEnabled());
        dto.setNotifications(notifications);

        // Nested Security Settings
        SecuritySettingsDto security = new SecuritySettingsDto();
        security.setTwoFactor(user.isSecurityTwoFactorEnabled());
        security.setSessionTimeout(user.getSecuritySessionTimeout());
        dto.setSecurity(security);

        // Last Login Field
        dto.setLastLogin(user.getLastLogin() != null ? user.getLastLogin().toString() : null);

        // Log profile view (optional)
        activityLogger.logSystemActivity("Profile viewed for user: " + username);

        return dto;
    }

    @Override
    @Transactional
    public MyProfileDto updateMyProfile(String username, UpdateMyProfileDto updateDetails) {
        // Find the user by username (authenticated user)
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + username));

        // Update fields from the DTO where the DTO field is not null
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
                throw new IllegalArgumentException("Invalid birthdate format. Use YYYY-MM-DD.", e);
            }
        }
        if (updateDetails.getOccupation() != null) {
            user.setOccupation(updateDetails.getOccupation());
        }
        if (updateDetails.getEducation() != null) {
            user.setEducation(updateDetails.getEducation());
        }
        if (updateDetails.getProfilePicture() != null) {
            user.setProfilePictureUrl(updateDetails.getProfilePicture());
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
            user.setSecuritySessionTimeout(securityDto.getSessionTimeout());
        }

        try {
            UserEntity updatedUser = userRepository.save(user);

            // Log profile update
            activityLogger.logProfileUpdate(username);

            // Map the updated user back to MyProfileDto for the response
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
            dto.setBirthdate(updatedUser.getBirthdate() != null ? updatedUser.getBirthdate().toString() : null);
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
            dto.setLastLogin(updatedUser.getLastLogin() != null ? updatedUser.getLastLogin().toString() : null);

            return dto;

        } catch (DataIntegrityViolationException e) {
            // Log error
            activityLogger.logSystemError("Data integrity violation during profile update for user " + username);

            // Catch unique constraint violations (e.g., duplicate email)
            throw new IllegalArgumentException("Data integrity violation: Email already in use or invalid data.", e);
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordDto changePasswordDto) {
        // Find the user by username (authenticated user)
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + username));

        // Verify the current password
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            // Log failed password change
            activityLogger.logSystemActivity("Failed password change attempt: incorrect current password for user " + username);

            throw new BadCredentialsException("Incorrect current password");
        }

        // Encode the new password
        String encodedNewPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());

        // Set the new encoded password on the user entity
        user.setPassword(encodedNewPassword);

        // Save the updated user
        userRepository.save(user);

        // Log password change
        activityLogger.logPasswordChange(username);
    }

    @Override
    @Transactional
    public void createSuperAdminIfNotExists() {
        final String superAdminUsername = "superadmin";
        if (!userRepository.existsByUsername(superAdminUsername)) {
            UserEntity superAdmin = new UserEntity();
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setName("Super Administrator");
            superAdmin.setEmail("superadmin@example.com");
            superAdmin.setPassword(passwordEncoder.encode("superadmin123"));
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

            // Log super admin creation
            activityLogger.logSystemActivity("Super Admin account created with username: " + superAdminUsername);
        }
    }
}
