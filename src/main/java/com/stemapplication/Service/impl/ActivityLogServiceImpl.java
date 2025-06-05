package com.stemapplication.Service.impl;

import com.stemapplication.DTO.ActivityLogDto;
import com.stemapplication.Models.ActivityLog;
import com.stemapplication.Repository.ActivityLogRepository;
import com.stemapplication.Service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int DEFAULT_CLEANUP_DAYS = 30;

    @Autowired
    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public ResponseEntity<List<ActivityLogDto>> getActivityLogs(Integer limit, String type) {
        try {
            Pageable pageable = PageRequest.of(0, limit != null ? limit : 20);
            List<ActivityLog> activityLogs;

            if (type != null && !type.trim().isEmpty()) {
                try {
                    ActivityLog.ActivityType activityType = ActivityLog.ActivityType.valueOf(type.toUpperCase());
                    activityLogs = activityLogRepository.findByTypeOrderByDateDesc(activityType, pageable);
                } catch (IllegalArgumentException e) {
                    // Invalid type provided, return empty list or handle error
                    return ResponseEntity.badRequest().build();
                }
            } else {
                activityLogs = activityLogRepository.findAllByOrderByDateDesc(pageable);
            }

            List<ActivityLogDto> activityLogDtos = activityLogs.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(activityLogDtos);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public void logUserActivity(String user, String action, Long entityId, String entityType) {
        logActivity(user, action, ActivityLog.ActivityType.USER, entityId, entityType);
    }

    @Override
    public void logContentActivity(String user, String action, Long entityId, String entityType) {
        logActivity(user, action, ActivityLog.ActivityType.CONTENT, entityId, entityType);
    }

    @Override
    public void logSystemActivity(String user, String action) {
        logActivity(user, action, ActivityLog.ActivityType.SYSTEM);
    }

    @Override
    public void logActivity(String user, String action, ActivityLog.ActivityType type, Long entityId, String entityType) {
        try {
            ActivityLog activityLog = new ActivityLog(user, action, type, entityId, entityType);
            activityLogRepository.save(activityLog);
        } catch (Exception e) {
            // Log the error but don't throw to avoid disrupting main operations
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }

    @Override
    public void logActivity(String user, String action, ActivityLog.ActivityType type) {
        logActivity(user, action, type, null, null);
    }

    @Override
    public ResponseEntity<List<ActivityLogDto>> getUserActivities(String username, Integer limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit != null ? limit : 20);
            List<ActivityLog> activityLogs = activityLogRepository.findByUserOrderByDateDesc(username, pageable);

            List<ActivityLogDto> activityLogDtos = activityLogs.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(activityLogDtos);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ActivityLogDto>> getEntityActivities(String entityType, Long entityId, Integer limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit != null ? limit : 20);
            List<ActivityLog> activityLogs = activityLogRepository
                    .findByEntityTypeAndEntityIdOrderByDateDesc(entityType, entityId, pageable);

            List<ActivityLogDto> activityLogDtos = activityLogs.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(activityLogDtos);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<?> getActivityStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();

            long totalActivities = activityLogRepository.count();
            long userActivities = activityLogRepository.countByType(ActivityLog.ActivityType.USER);
            long contentActivities = activityLogRepository.countByType(ActivityLog.ActivityType.CONTENT);
            long systemActivities = activityLogRepository.countByType(ActivityLog.ActivityType.SYSTEM);

            // Add cleanup-related statistics
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(DEFAULT_CLEANUP_DAYS);
            long oldLogs = activityLogRepository.countByDateBefore(thirtyDaysAgo);
            LocalDateTime oldestLog = activityLogRepository.findOldestLogDate();

            stats.put("total", totalActivities);
            stats.put("user", userActivities);
            stats.put("content", contentActivities);
            stats.put("system", systemActivities);
            stats.put("logsOlderThan30Days", oldLogs);
            stats.put("oldestLogDate", oldestLog != null ? oldestLog.format(DATE_FORMATTER) : null);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============= CLEANUP METHODS =============

    @Override
    public int cleanupOldLogs(int daysOld) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            int deletedCount = activityLogRepository.deleteByDateBefore(cutoffDate);

            if (deletedCount > 0) {
                System.out.println("Activity log cleanup: Deleted " + deletedCount + " logs older than " + daysOld + " days");
                logSystemActivity("SYSTEM", "Cleaned up " + deletedCount + " old activity logs (older than " + daysOld + " days)");
            }

            return deletedCount;
        } catch (Exception e) {
            System.err.println("Failed to cleanup old activity logs: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public long getLogCount() {
        return activityLogRepository.count();
    }

    @Override
    public ResponseEntity<?> performManualCleanup(Integer daysOld) {
        try {
            int days = daysOld != null ? daysOld : DEFAULT_CLEANUP_DAYS;

            // Get count before cleanup
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
            long logsToDelete = activityLogRepository.countByDateBefore(cutoffDate);

            if (logsToDelete == 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No logs found older than " + days + " days");
                response.put("deletedCount", 0);
                return ResponseEntity.ok(response);
            }

            int deletedCount = cleanupOldLogs(days);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully cleaned up old activity logs");
            response.put("deletedCount", deletedCount);
            response.put("daysOld", days);
            response.put("cutoffDate", cutoffDate.format(DATE_FORMATTER));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to perform cleanup: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // ============= SCHEDULED CLEANUP =============

    /**
     * Automatically cleanup logs older than 30 days
     * Runs daily at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledCleanup() {
        System.out.println("Starting scheduled activity log cleanup...");
        try {
            long totalLogsBefore = getLogCount();
            int deletedCount = cleanupOldLogs(DEFAULT_CLEANUP_DAYS);
            long totalLogsAfter = getLogCount();

            System.out.println("Scheduled cleanup completed: " + deletedCount + " logs deleted. " +
                    "Total logs before: " + totalLogsBefore + ", after: " + totalLogsAfter);

        } catch (Exception e) {
            System.err.println("Scheduled cleanup failed: " + e.getMessage());
            logSystemActivity("SYSTEM", "Scheduled activity log cleanup failed: " + e.getMessage());
        }
    }

    private ActivityLogDto mapToDto(ActivityLog activityLog) {
        ActivityLogDto dto = new ActivityLogDto();
        dto.setId(activityLog.getId().toString());
        dto.setUser(activityLog.getUser());
        dto.setAction(activityLog.getAction());
        dto.setDate(activityLog.getDate().format(DATE_FORMATTER));
        dto.setType(activityLog.getType().name().toLowerCase());
        dto.setEntityId(activityLog.getEntityId());
        dto.setEntityType(activityLog.getEntityType());
        dto.setIpAddress(activityLog.getIpAddress());
        return dto;
    }
}