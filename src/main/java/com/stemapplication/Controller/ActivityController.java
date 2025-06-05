package com.stemapplication.Controller;

import com.stemapplication.DTO.ActivityLogDto;
import com.stemapplication.Service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private final ActivityLogService activityLogService;

    @Autowired
    public ActivityController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    /**
     * Get recent system activity logs
     * Available for SuperAdmin and Admin dashboards
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ActivityLogDto>> getActivityLogs(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String type) {

        return activityLogService.getActivityLogs(limit, type);
    }

    /**
     * Get activities for a specific user
     */
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ActivityLogDto>> getUserActivities(
            @PathVariable String username,
            @RequestParam(required = false) Integer limit) {

        return activityLogService.getUserActivities(username, limit);
    }

    /**
     * Get activities for a specific entity (e.g., gallery item, blog post)
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ActivityLogDto>> getEntityActivities(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam(required = false) Integer limit) {

        return activityLogService.getEntityActivities(entityType, entityId, limit);
    }

    /**
     * Get activity statistics including cleanup information
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> getActivityStatistics() {
        return activityLogService.getActivityStatistics();
    }

    // ============= CLEANUP ENDPOINTS =============

    /**
     * Manually trigger cleanup of old activity logs
     * Only accessible by Super Admin
     */
    @PostMapping("/cleanup")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> manualCleanup(
            @RequestParam(required = false) Integer daysOld) {

        return activityLogService.performManualCleanup(daysOld);
    }

    /**
     * Get cleanup preview - shows how many logs would be deleted
     */
    @GetMapping("/cleanup/preview")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> getCleanupPreview(
            @RequestParam(required = false, defaultValue = "30") Integer daysOld) {

        // This endpoint shows what would be cleaned up without actually doing it
        // Implementation would be similar to the statistics endpoint but focused on cleanup data
        return activityLogService.getActivityStatistics();
    }
}