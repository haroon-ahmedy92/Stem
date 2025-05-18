package com.stemapplication.DTO;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record AdminProfileDTO(
        // Personal Information Section
        @NotBlank(message = "Full name cannot be blank")
        @Size(max = 100, message = "Full name must be less than 100 characters")
        String fullName,

        @Pattern(
                regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s./0-9]*$",
                message = "Invalid phone number format"
        )
        @Size(max = 20, message = "Phone number too long")
        String phoneNumber,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Size(max = 100, message = "Occupation too long")
        String occupation,

        @Size(max = 500, message = "Bio too long")
        String bio,

        // Contact Information Section
        @Size(max = 200, message = "Address too long")
        String address,

        @Size(max = 100, message = "Education info too long")
        String education,

        // Department Info
        @NotBlank(message = "Department cannot be blank")
        @Size(max = 100, message = "Department name too long")
        String department
) {
    // Builder pattern for easy creation
    public static class Builder {
        private String fullName;
        private String phoneNumber;
        private LocalDate dateOfBirth;
        private String occupation;
        private String bio;
        private String address;
        private String education;
        private String department;

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        // ... other builder methods ...

        public AdminProfileDTO build() {
            return new AdminProfileDTO(
                    fullName, phoneNumber, dateOfBirth, occupation,
                    bio, address, education, department
            );
        }
    }
}