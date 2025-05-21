package com.stemapplication.DTO;

import lombok.Data;

// DTO for Security Settings (used as a nested object)
@Data
public class SecuritySettingsDto {
    private boolean twoFactor;
    private String sessionTimeout; // e.g., "30m", "1h"
    // These fields correspond to individual fields in UserEntity
}