package com.stemapplication.Service;

import com.stemapplication.DTO.*;
import com.stemapplication.Models.Admin;
import com.stemapplication.Repository.AdminRepository;
import com.stemapplication.Exceptions.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ADMIN REGISTRATION
    public Admin registerAdmin(Admin admin, String role) {
        validateAdminDoesNotExist(admin.getUsername(), admin.getEmail());

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.addRole("ROLE_" + role.toUpperCase());
        return adminRepository.save(admin);
    }

    // PROFILE MANAGEMENT
    public AdminProfileResponseDTO getAdminProfile(String username) {
        Admin admin = getAdminByUsername(username);
        return convertToProfileDTO(admin);
    }

    public AdminProfileResponseDTO updateAdminProfile(String username, AdminProfileDTO dto) {
        Admin admin = getAdminByUsername(username);

        updateProfileFields(admin, dto);
        return convertToProfileDTO(adminRepository.save(admin));
    }

    // PASSWORD MANAGEMENT
    public void changeAdminPassword(String username, AdminPasswordDTO dto) {
        Admin admin = getAdminByUsername(username);

        validateCurrentPassword(admin, dto.currentPassword());
        admin.setPassword(passwordEncoder.encode(dto.newPassword()));
        adminRepository.save(admin);
    }

    // ADMIN MANAGEMENT
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with id: " + id));
    }

    public Admin updateAdmin(Long id, Admin adminDetails) {
        Admin admin = getAdminById(id);

        if (!admin.getEmail().equals(adminDetails.getEmail())) {
            validateEmail(adminDetails.getEmail());
        }

        updateAdminFields(admin, adminDetails);
        return adminRepository.save(admin);
    }

    public void deleteAdmin(Long id) {
        adminRepository.delete(getAdminById(id));
    }

    // AUTHORIZATION
    public boolean isAuthorizedToPost(String username) {
        Admin admin = getAdminByUsername(username);
        return admin.getRoles().contains("ROLE_ADMIN") ||
                admin.getRoles().contains("ROLE_SUPER_ADMIN");
    }

    // PRIVATE HELPER METHODS
    private Admin getAdminByUsername(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found"));
    }

    private void validateAdminDoesNotExist(String username, String email) {
        if (adminRepository.existsByUsername(username)) {
            throw new AdminAlreadyExistsException("Username already exists");
        }
        if (adminRepository.existsByEmail(email)) {
            throw new AdminAlreadyExistsException("Email already exists");
        }
    }

    private void validateEmail(String email) {
        if (adminRepository.existsByEmail(email)) {
            throw new AdminAlreadyExistsException("Email already in use");
        }
    }

    private void validateCurrentPassword(Admin admin, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
    }

    private void updateProfileFields(Admin admin, AdminProfileDTO dto) {
        admin.setFullName(dto.fullName());
        admin.setPhoneNumber(dto.phoneNumber());
        admin.setDateOfBirth(dto.dateOfBirth());
        admin.setOccupation(dto.occupation());
        admin.setBio(dto.bio());
        admin.setAddress(dto.address());
        admin.setEducation(dto.education());
        admin.setDepartment(dto.department());
    }

    private void updateAdminFields(Admin admin, Admin adminDetails) {
        admin.setUsername(adminDetails.getUsername());
        admin.setEmail(adminDetails.getEmail());

        if (adminDetails.getPassword() != null && !adminDetails.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
        }
    }

    private AdminProfileResponseDTO convertToProfileDTO(Admin admin) {
        return new AdminProfileResponseDTO(
                admin.getFullName(),
                admin.getPhoneNumber(),
                admin.getDateOfBirth(),
                admin.getOccupation(),
                admin.getBio(),
                admin.getEmail(),
                admin.getAddress(),
                admin.getRoles().iterator().next(),
                admin.getEducation(),
                admin.getDepartment()
        );
    }
}