package com.stemapplication.Utils;

import com.stemapplication.Models.ActivityLog;
import com.stemapplication.Service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class to help with logging activities across the application.
 * Provides convenient methods for different types of activities.
 */
@Component
public class ActivityLogger {

    private final ActivityLogService activityLogService;

    @Autowired
    public ActivityLogger(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    // ==================== AUTH ACTIVITIES ====================

    /**
     * Log a successful login
     */
    public void logLogin(String username, Long userId) {
        activityLogService.logUserActivity(username, "User logged in", userId, "user");
    }

    /**
     * Log a failed login attempt
     */
    public void logFailedLogin(String username, String reason) {
        activityLogService.logSystemActivity("SYSTEM", "Failed login attempt by " + username + ": " + reason);
    }

    /**
     * Log a logout
     */
    public void logLogout(String username) {
        activityLogService.logUserActivity(username, "User logged out", null, null);
    }

    /**
     * Log user registration
     */
    public void logRegistration(String username) {
        activityLogService.logUserActivity(username, "New user registered (pending approval)", null, null);
    }

    /**
     * Log password change
     */
    public void logPasswordChange(String username) {
        activityLogService.logUserActivity(username, "Password changed", null, null);
    }

    /**
     * Log profile update
     */
    public void logProfileUpdate(String username) {
        activityLogService.logUserActivity(username, "Profile information updated", null, null);
    }

    // ==================== USER MANAGEMENT ACTIVITIES ====================

    /**
     * Log user approval
     */
    public void logUserApproval(String adminUsername, String targetUser, Long targetUserId) {
        activityLogService.logUserActivity(adminUsername, "Approved user account: " + targetUser, targetUserId, "user");
    }

    /**
     * Log user suspension
     */
    public void logUserSuspension(String adminUsername, String targetUser, Long targetUserId) {
        activityLogService.logUserActivity(adminUsername, "Suspended user account: " + targetUser, targetUserId, "user");
    }

    /**
     * Log role promotion
     */
    public void logRolePromotion(String adminUsername, String targetUser, Long targetUserId, String newRole) {
        activityLogService.logUserActivity(adminUsername, "Promoted user to " + newRole + ": " + targetUser, targetUserId, "user");
    }

    /**
     * Log role demotion
     */
    public void logRoleDemotion(String adminUsername, String targetUser, Long targetUserId) {
        activityLogService.logUserActivity(adminUsername, "Demoted user from admin role: " + targetUser, targetUserId, "user");
    }

    /**
     * Log user deletion
     */
    public void logUserDeletion(String adminUsername, String targetUser, Long targetUserId) {
        activityLogService.logUserActivity(adminUsername, "Deleted user account: " + targetUser, targetUserId, "user");
    }

    // ==================== BLOG CONTENT ACTIVITIES ====================

    /**
     * Log post creation
     */
    public void logPostCreation(String username, String postTitle, Long postId) {
        activityLogService.logContentActivity(username, "Created blog post: " + postTitle, postId, "post");
    }

    /**
     * Log post update
     */
    public void logPostUpdate(String username, String postTitle, Long postId) {
        activityLogService.logContentActivity(username, "Updated blog post: " + postTitle, postId, "post");
    }

    /**
     * Log post deletion
     */
    public void logPostDeletion(String username, String postTitle, Long postId) {
        activityLogService.logContentActivity(username, "Deleted blog post: " + postTitle, postId, "post");
    }

    // ==================== GALLERY ACTIVITIES ====================

    /**
     * Log gallery item creation
     */
    public void logGalleryCreation(String username, String itemTitle, Long itemId) {
        activityLogService.logContentActivity(username, "Created gallery item: " + itemTitle, itemId, "gallery");
    }

    /**
     * Log gallery item update
     */
    public void logGalleryUpdate(String username, String itemTitle, Long itemId) {
        activityLogService.logContentActivity(username, "Updated gallery item: " + itemTitle, itemId, "gallery");
    }

    /**
     * Log gallery item deletion
     */
    public void logGalleryDeletion(String username, String itemTitle, Long itemId) {
        activityLogService.logContentActivity(username, "Deleted gallery item: " + itemTitle, itemId, "gallery");
    }

    // ==================== COMMENT ACTIVITIES ====================

    /**
     * Log comment creation
     */
    public void logCommentCreation(String username, Long postId, Long commentId) {
        activityLogService.logContentActivity(username, "Added comment to post", commentId, "comment");
    }

    /**
     * Log comment approval
     */
    public void logCommentApproval(String moderatorUsername, Long commentId, Long postId) {
        activityLogService.logContentActivity(moderatorUsername, "Approved comment", commentId, "comment");
    }

    /**
     * Log comment deletion
     */
    public void logCommentDeletion(String moderatorUsername, Long commentId, Long postId) {
        activityLogService.logContentActivity(moderatorUsername, "Deleted comment", commentId, "comment");
    }

    // ==================== SYSTEM ACTIVITIES ====================

    /**
     * Log general system activity
     */
    public void logSystemActivity(String action) {
        activityLogService.logSystemActivity("SYSTEM", action);
    }

    /**
     * Log system error
     */
    public void logSystemError(String error) {
        activityLogService.logSystemActivity("SYSTEM", "Error: " + error);
    }
}
