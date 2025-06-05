package com.stemapplication.Service;

import com.stemapplication.DTO.ActivityLogDto;
import com.stemapplication.Models.ActivityLog;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ActivityLogService {

    // Retrieval methods
    ResponseEntity<List<ActivityLogDto>> getActivityLogs(Integer limit, String type);
    ResponseEntity<List<ActivityLogDto>> getUserActivities(String username, Integer limit);
    ResponseEntity<List<ActivityLogDto>> getEntityActivities(String entityType, Long entityId, Integer limit);
    ResponseEntity<?> getActivityStatistics();

    // Logging methods
    void logUserActivity(String user, String action, Long entityId, String entityType);
    void logContentActivity(String user, String action, Long entityId, String entityType);
    void logSystemActivity(String user, String action);
    void logActivity(String user, String action, ActivityLog.ActivityType type, Long entityId, String entityType);
    void logActivity(String user, String action, ActivityLog.ActivityType type);

    // Cleanup methods
    int cleanupOldLogs(int daysOld);
    long getLogCount();
    ResponseEntity<?> performManualCleanup(Integer daysOld);
}