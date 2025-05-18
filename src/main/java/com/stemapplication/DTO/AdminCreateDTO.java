package com.stemapplication.DTO;

import jakarta.validation.constraints.*;
import lombok.*;


@Setter
@Getter
public class AdminCreateDTO {
    // Getters and Setters
    @NotBlank @Size(min = 3, max = 50)
    private String username;

    @NotBlank @Size(min = 8)
    private String password;

    @NotBlank @Email
    private String email;


}