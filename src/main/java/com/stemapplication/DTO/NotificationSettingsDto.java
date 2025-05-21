package com.stemapplication.DTO;

import lombok.Data;

// DTO for Notification Settings (used as a nested object)
@Data
public class NotificationSettingsDto {
    private boolean email;
    private boolean app;
    private boolean updates;
    // These fields correspond to individual boolean fields in UserEntity
}