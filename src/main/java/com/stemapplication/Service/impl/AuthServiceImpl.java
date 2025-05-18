package com.stemapplication.Service.impl;

import com.stemapplication.DTO.AuthResponseDto;
import com.stemapplication.DTO.LoginDto;
import com.stemapplication.DTO.RegisterDto;
import com.stemapplication.Models.RefreshToken;
import com.stemapplication.Models.Role;
import com.stemapplication.Models.UserEntity;
import com.stemapplication.Repository.RoleRepository;
import com.stemapplication.Repository.UserRepository;
import com.stemapplication.Security.JWTGenerator;
import com.stemapplication.Service.AuthService;
import com.stemapplication.Service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
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
    //@Transactional // Uncomment this if it's commented out
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
                    .secure(true) // Set to true in production (HTTPS)
                    .path("/api/auth/refresh-token") // Accessible only by refresh token endpoint
                    .maxAge(refreshToken.getExpiryDate().getEpochSecond() - Instant.now().getEpochSecond()) // duration in seconds
                    .sameSite("Strict") // Or "Lax" depending on your needs
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());
            System.out.println("Refresh Cookie added to response"); // Added logging


            return ResponseEntity.ok(new AuthResponseDto(token));

        } catch (BadCredentialsException e) {
            System.out.println("Login failed: Invalid credentials for user: " + loginDto.getUsername()); // Added logging
            return new ResponseEntity<>("Invalid username or password!", HttpStatus.UNAUTHORIZED);

        } catch (DisabledException e) {
            // This should now primarily be caught if your CustomUserDetailsService directly throws DisabledException
            System.out.println("Login failed: Disabled account for user: " + loginDto.getUsername()); // Added logging
            return new ResponseEntity<>("User account is not approved yet or disabled.", HttpStatus.FORBIDDEN); // Use 403 Forbidden

        } catch (InternalAuthenticationServiceException e) { // <-- Catch the wrapper exception
            // Check if the cause is the DisabledException
            if (e.getCause() instanceof DisabledException) {
                System.out.println("Login failed: Internal service exception (Disabled account) for user: " + loginDto.getUsername()); // Added logging
                return new ResponseEntity<>("User account is not approved yet or disabled.", HttpStatus.FORBIDDEN); // Return 403 Forbidden

            } else {
                // If it's another type of InternalAuthenticationServiceException, re-throw or handle as internal error
                System.err.println("Login failed: Unexpected InternalAuthenticationServiceException for user: " + loginDto.getUsername()); // Added logging
                e.printStackTrace(); // Print stack trace for debugging
                return new ResponseEntity<>("An internal authentication service error occurred.", HttpStatus.INTERNAL_SERVER_ERROR); // Return 500
            }

        } catch (Exception e) { // <-- Catch any other unexpected exceptions during the process *after* authenticate()
            System.err.println("Login failed: An unexpected error occurred after authentication for user: " + loginDto.getUsername()); // Added logging
            e.printStackTrace(); // Print stack trace for debugging
            return new ResponseEntity<>("An unexpected error occurred during login.", HttpStatus.INTERNAL_SERVER_ERROR); // Return 500
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> register(RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is already in use!", HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>("User registered successfully! Awaiting approval.", HttpStatus.OK);
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found in cookie.");
        }

        try {
            // Find the token in the database
            Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(providedRefreshToken);

            if (!refreshTokenOptional.isPresent()) {
                System.out.println("Invalid refresh token: Token not found in DB for token: " + providedRefreshToken); // Add logging
                // Optionally delete a potentially malformed cookie if it exists but token isn't in DB
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).path("/api/auth/refresh-token").httpOnly(true).secure(true).sameSite("Strict").build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
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
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).path("/api/auth/refresh-token").httpOnly(true).secure(true).sameSite("Strict").build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: User not associated.");
            }

            // Check if the user account is approved/enabled
            if (!userEntity.isApproved()) { // Assuming isApproved() is the check you need
                System.out.println("Refresh token valid but user account not approved: " + userEntity.getUsername()); // Add logging
                // Optionally delete refresh token for disabled user
                refreshTokenService.deleteByToken(providedRefreshToken);
                ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).path("/api/auth/refresh-token").httpOnly(true).secure(true).sameSite("Strict").build();
                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is not approved yet or disabled.");
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
                    .secure(true) // Use true in production with HTTPS
                    .path("/api/auth/refresh-token") // Set path appropriately
                    .maxAge(newRefreshToken.getExpiryDate().getEpochSecond() - Instant.now().getEpochSecond())
                    .sameSite("Strict") // Choose Strict or Lax based on needs
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString());
            System.out.println("New Refresh Cookie set for user: " + userEntity.getUsername()); // Add logging


            // Return the new access token in the body
            return ResponseEntity.ok(new AuthResponseDto(newAccessToken));

        } catch (ExpiredJwtException e) { // Catch JJWT specific expired exception if verifyExpiration throws it
            System.out.println("Refresh token expired for token: " + providedRefreshToken); // Add logging
            // Clean up expired token from DB if verifyExpiration didn't already
            refreshTokenService.deleteByToken(providedRefreshToken); // Ensure it's deleted
            ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).path("/api/auth/refresh-token").httpOnly(true).secure(true).sameSite("Strict").build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired. Please log in again.");

        } catch (RuntimeException e) { // Catch RuntimeExceptions from verifyExpiration (if it throws generic) or other issues
            System.err.println("Runtime error during refresh token process for token: " + providedRefreshToken + " - " + e.getMessage()); // Add logging
            e.printStackTrace(); // Print stack trace
            // Decide how to handle - often treated as invalid token or internal error
            ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).path("/api/auth/refresh-token").httpOnly(true).secure(true).sameSite("Strict").build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token."); // Or INTERNAL_SERVER_ERROR depending on expected cause
        } catch (Exception e) { // Catch any other unexpected exceptions
            System.err.println("An unexpected error occurred during refresh token process for token: " + providedRefreshToken); // Add logging
            e.printStackTrace();
            ResponseCookie expiredCookie = ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).path("/api/auth/refresh-token").httpOnly(true).secure(true).sameSite("Strict").build();
            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during token refresh.");
        }
    }


    @Override
    @Transactional
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
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
                .secure(true) // Set to true in production
                .path("/api/auth/refresh-token")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        SecurityContextHolder.clearContext(); // Clear security context
        return ResponseEntity.ok("Logged out successfully.");
    }


    @Override
    @Transactional
    public ResponseEntity<String> approveUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setApproved(true);
        userRepository.save(user);
        return ResponseEntity.ok("User " + user.getUsername() + " approved successfully.");
    }

    @Override
    @Transactional
    public ResponseEntity<String> promoteToAdmin(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found. Please create it."));

        // Avoid duplicate roles
        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            user.getRoles().add(adminRole);
            // If user was only ROLE_USER and now becomes ROLE_ADMIN, you might want to remove ROLE_USER
            // or keep both depending on your privilege system. For simplicity, we add.
            // If they were pending and only had ROLE_USER, ensure they are approved too.
            if(!user.isApproved()){
                user.setApproved(true);
            }
            userRepository.save(user);
            return ResponseEntity.ok("User " + user.getUsername() + " promoted to Admin successfully.");
        }
        return ResponseEntity.badRequest().body("User " + user.getUsername() + " is already an Admin or promotion failed.");
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