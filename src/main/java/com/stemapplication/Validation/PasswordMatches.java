package com.stemapplication.Validation;

import jakarta.validation.Constraint; // Import Constraint
import jakarta.validation.Payload; // Import Payload

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) // Can be applied to a class or another annotation
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
@Constraint(validatedBy = PasswordMatchesValidator.class) // Specify the validator class
@Documented // Include in Javadoc
public @interface PasswordMatches {
    String message() default "Passwords do not match"; // Default error message
    Class<?>[] groups() default {}; // Validation groups
    Class<? extends Payload>[] payload() default {}; // Payload for validation

    // Optional: Allow specifying fields to compare if DTO structure varies
    String newPasswordField() default "newPassword";
    String confirmPasswordField() default "confirmPassword";

    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        PasswordMatches[] value();
    }
}