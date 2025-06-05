package com.stemapplication.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDto {
    private String id;
    private String user;
    private String action;
    private String date;
    private String type;
    private Long entityId;
    private String entityType;
    private String ipAddress;
}