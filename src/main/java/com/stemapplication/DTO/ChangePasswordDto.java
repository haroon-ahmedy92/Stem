package com.stemapplication.DTO;

import com.stemapplication.Validation.PasswordMatches; // We'll create this annotation
import jakarta.validation.constraints.NotBlank; // Import validation annotations
import lombok.Data;

@Data
@PasswordMatches // Custom validation to check if new and confirm passwords match
public class ChangePasswordDto {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    // Add size constraints if needed, e.g., @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

}