package com.stemapplication.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String user;

    @Column(nullable = false, length = 500)
    private String action;

    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    // Additional metadata fields
    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    public enum ActivityType {
        CONTENT, SYSTEM, USER
    }

    // Constructor for easy creation
    public ActivityLog(String user, String action, ActivityType type) {
        this.user = user;
        this.action = action;
        this.type = type;
        this.date = LocalDateTime.now();
    }

    public ActivityLog(String user, String action, ActivityType type, Long entityId, String entityType) {
        this(user, action, type);
        this.entityId = entityId;
        this.entityType = entityType;
    }
}
