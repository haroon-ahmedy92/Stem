package com.stemapplication.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Setter
@Getter
public class AdminUpdateDTO {
    // Getters and Setters
    @NotBlank @Size(min = 3, max = 50)
        private String username;

        @Size(min = 8)
        private String password;  // Optional

        @NotBlank @Email
        private String email;

}