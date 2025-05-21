package com.stemapplication.Service.impl;

import com.stemapplication.DTO.*;
import com.stemapplication.Models.Role;
import com.stemapplication.Models.UserEntity;
import com.stemapplication.Repository.RoleRepository;
import com.stemapplication.Repository.UserRepository;
import com.stemapplication.Service.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService implements com.stemapplication.Service.AdminService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;

    public AdminService(UserRepository userRepository,RoleRepository roleRepository, RefreshTokenService refreshTokenService ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
    }



    @Override
    public List<AdminUserDto> getAllAdminUsers() {

        List<UserEntity> allUsers = userRepository.findAll();

        // Filter for users who have ROLE_ADMIN or ROLE_SUPER_ADMIN
        List<UserEntity> adminUsers = allUsers.stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_SUPER_ADMIN")))
                .collect(Collectors.toList());

        // Map UserEntity to AdminUserDto
        return adminUsers.stream()
                .map(user -> {
                    AdminUserDto dto = new AdminUserDto();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setDepartment(user.getDepartment());
                    dto.setStatus(user.isApproved() ? "Approved" : "Pending");
                    dto.setRoles(user.getRoles().stream()
                            .map(role -> role.getName())
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }







    @Override
    public List<UserProfileDto> getAllUsers() {
        List<UserEntity> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(this::mapUserToUserProfileDto)
                .collect(Collectors.toList());
    }

    private UserProfileDto mapUserToUserProfileDto(UserEntity user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDepartment(user.getDepartment());
        dto.setStatus(user.isApproved() ? "Approved" : "Pending");
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        dto.setPhone(user.getPhone());

        return dto;
    }





    @Override
    @Transactional
    public AdminUserDto updateUserDetails(Long userId, UpdateUserDto updateDetails) {
        // 1. Find the user by ID
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId)); // Throw a standard JPA exception for 404

        // 2. Update simple fields
        if (updateDetails.getName() != null) {
            user.setName(updateDetails.getName());
        }
        if (updateDetails.getEmail() != null) {
            user.setEmail(updateDetails.getEmail());
        }
        if (updateDetails.getDepartment() != null) {
            user.setDepartment(updateDetails.getDepartment());
        }
        if (updateDetails.getStatus() != null) {
            user.setApproved(updateDetails.getStatus().equalsIgnoreCase("Approved")); // Case-insensitive check
        }

        // 3. Handle role update (interpreting single string input as replacing roles)
        if (updateDetails.getRole() != null) {
            Role newRole = roleRepository.findByName(updateDetails.getRole())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + updateDetails.getRole()));

            // *** WARNING: This replaces ALL existing roles with the single new role ***
            // A more robust approach would be add/remove specific roles or handle a list of roles.
            user.getRoles().clear(); // Clear existing roles
            user.getRoles().add(newRole); // Add the new role
        }


        // 4. Save the updated user
        try {
            UserEntity updatedUser = userRepository.save(user);

            // 5. Map the updated user back to AdminUserDto for the response
            AdminUserDto dto = new AdminUserDto();
            dto.setId(updatedUser.getId());
            dto.setName(updatedUser.getName());
            dto.setEmail(updatedUser.getEmail());
            dto.setDepartment(updatedUser.getDepartment());
            dto.setStatus(updatedUser.isApproved() ? "Approved" : "Pending");
            dto.setRoles(updatedUser.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList()));

            return dto;

        } catch (DataIntegrityViolationException e) {
            // Catch unique constraint violations (e.g., duplicate email)
            // You might want to inspect the exception message to be more specific
            throw new IllegalArgumentException("Data integrity violation: " + e.getRootCause().getMessage(), e); // Throw IllegalArgumentException for 400
        }


    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // 1. Find the user by ID to ensure they exist
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId)); // Throw a standard JPA exception for 404

        // 2. Delete related Refresh Tokens for this user
        refreshTokenService.deleteByUserId(userId); // Use the method we added earlier

        // 3. Delete the User entity
        userRepository.delete(user); // Delete the found entity

        // JPA/Hibernate should handle the deletion of entries in the user_roles
        // join table automatically when the UserEntity is deleted, provided the
        // relationship is configured correctly on the UserEntity side.
        // If you used CascadeType.REMOVE or ALL on the roles collection in UserEntity,
        // this should happen automatically. Otherwise, you might need to clear the
        // roles collection on the user entity before deleting the user, or add a
        // custom query to delete from user_roles by user_id.
        // Let's assume standard JPA behavior cleans up the join table entries.

        System.out.println("User deleted successfully with ID: " + userId); // Add logging
    }



//    private final AdminRepository adminRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
//        this.adminRepository = adminRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    // ADMIN REGISTRATION
//    public Admin registerAdmin(Admin admin, String role) {
//        validateAdminDoesNotExist(admin.getUsername(), admin.getEmail());
//
//        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
//        admin.addRole("ROLE_" + role.toUpperCase());
//        return adminRepository.save(admin);
//    }
//
//    // PROFILE MANAGEMENT
//    public AdminProfileResponseDTO getAdminProfile(String username) {
//        Admin admin = getAdminByUsername(username);
//        return convertToProfileDTO(admin);
//    }
//
//    public AdminProfileResponseDTO updateAdminProfile(String username, AdminProfileDTO dto) {
//        Admin admin = getAdminByUsername(username);
//
//        updateProfileFields(admin, dto);
//        return convertToProfileDTO(adminRepository.save(admin));
//    }
//
//    // PASSWORD MANAGEMENT
//    public void changeAdminPassword(String username, AdminPasswordDTO dto) {
//        Admin admin = getAdminByUsername(username);
//
//        validateCurrentPassword(admin, dto.currentPassword());
//        admin.setPassword(passwordEncoder.encode(dto.newPassword()));
//        adminRepository.save(admin);
//    }

//    // ADMIN MANAGEMENT
//    public List<Admin> getAllAdmins() {
//        return adminRepository.findAll();
//    }

//    public Admin getAdminById(Long id) {
//        return adminRepository.findById(id)
//                .orElseThrow(() -> new AdminNotFoundException("Admin not found with id: " + id));
//    }
//
//    public Admin updateAdmin(Long id, Admin adminDetails) {
//        Admin admin = getAdminById(id);
//
//        if (!admin.getEmail().equals(adminDetails.getEmail())) {
//            validateEmail(adminDetails.getEmail());
//        }
//
//        updateAdminFields(admin, adminDetails);
//        return adminRepository.save(admin);
//    }
//
//    public void deleteAdmin(Long id) {
//        adminRepository.delete(getAdminById(id));
//    }
//
//    // AUTHORIZATION
//    public boolean isAuthorizedToPost(String username) {
//        Admin admin = getAdminByUsername(username);
//        return admin.getRoles().contains("ROLE_ADMIN") ||
//                admin.getRoles().contains("ROLE_SUPER_ADMIN");
//    }
//
//    // PRIVATE HELPER METHODS
//    private Admin getAdminByUsername(String username) {
//        return adminRepository.findByUsername(username)
//                .orElseThrow(() -> new AdminNotFoundException("Admin not found"));
//    }
//
//    private void validateAdminDoesNotExist(String username, String email) {
//        if (adminRepository.existsByUsername(username)) {
//            throw new AdminAlreadyExistsException("Username already exists");
//        }
//        if (adminRepository.existsByEmail(email)) {
//            throw new AdminAlreadyExistsException("Email already exists");
//        }
//    }
//
//    private void validateEmail(String email) {
//        if (adminRepository.existsByEmail(email)) {
//            throw new AdminAlreadyExistsException("Email already in use");
//        }
//    }
//
//    private void validateCurrentPassword(Admin admin, String currentPassword) {
//        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
//            throw new InvalidPasswordException("Current password is incorrect");
//        }
//    }
//
//    private void updateProfileFields(Admin admin, AdminProfileDTO dto) {
//        admin.setFullName(dto.fullName());
//        admin.setPhoneNumber(dto.phoneNumber());
//        admin.setDateOfBirth(dto.dateOfBirth());
//        admin.setOccupation(dto.occupation());
//        admin.setBio(dto.bio());
//        admin.setAddress(dto.address());
//        admin.setEducation(dto.education());
//        admin.setDepartment(dto.department());
//    }
//
//    private void updateAdminFields(Admin admin, Admin adminDetails) {
//        admin.setUsername(adminDetails.getUsername());
//        admin.setEmail(adminDetails.getEmail());
//
//        if (adminDetails.getPassword() != null && !adminDetails.getPassword().isEmpty()) {
//            admin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
//        }
//    }
//
//    private AdminProfileResponseDTO convertToProfileDTO(Admin admin) {
//        return new AdminProfileResponseDTO(
//                admin.getFullName(),
//                admin.getPhoneNumber(),
//                admin.getDateOfBirth(),
//                admin.getOccupation(),
//                admin.getBio(),
//                admin.getEmail(),
//                admin.getAddress(),
//                admin.getRoles().iterator().next(),
//                admin.getEducation(),
//                admin.getDepartment()
//        );
//    }



}