package com.stemapplication.Validation;

import com.stemapplication.DTO.ChangePasswordDto; // Import your DTO
import jakarta.validation.ConstraintValidator; // Import ConstraintValidator
import jakarta.validation.ConstraintValidatorContext; // Import ConstraintValidatorContext


// Validator for the @PasswordMatches annotation on ChangePasswordDto
public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> { // Object because it validates the DTO object

    private String newPasswordField;
    private String confirmPasswordField;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // Initialize with field names from the annotation
        this.newPasswordField = constraintAnnotation.newPasswordField();
        this.confirmPasswordField = constraintAnnotation.confirmPasswordField();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        // Check if the object is an instance of your DTO
        if (!(obj instanceof ChangePasswordDto)) {
            return false; // Not the correct object type
        }

        ChangePasswordDto user = (ChangePasswordDto) obj;

        // Get the values of the new and confirm password fields
        String newPassword = user.getNewPassword();
        String confirmPassword = user.getConfirmPassword();

        // Check if both fields are non-null and equal
        boolean isValid = newPassword != null && confirmPassword != null && newPassword.equals(confirmPassword);

        // If validation fails, customize the error message placement (optional)
        if (!isValid) {
            context.disableDefaultConstraintViolation(); // Disable default message
            // Add the violation to the confirm password field
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(confirmPasswordField)
                    .addConstraintViolation();
        }

        return isValid; // Return true if valid, false otherwise
    }
}