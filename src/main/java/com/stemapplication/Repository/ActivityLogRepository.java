package com.stemapplication.Repository;

import com.stemapplication.Models.ActivityLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Find by type with pagination, ordered by date descending
    List<ActivityLog> findByTypeOrderByDateDesc(ActivityLog.ActivityType type, Pageable pageable);

    // Find all with pagination, ordered by date descending
    List<ActivityLog> findAllByOrderByDateDesc(Pageable pageable);

    // Find recent activities by user
    List<ActivityLog> findByUserOrderByDateDesc(String user, Pageable pageable);

    // Find activities within date range
    @Query("SELECT a FROM ActivityLog a WHERE a.date BETWEEN :startDate AND :endDate ORDER BY a.date DESC")
    List<ActivityLog> findByDateBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    // Find activities by entity
    List<ActivityLog> findByEntityTypeAndEntityIdOrderByDateDesc(String entityType, Long entityId, Pageable pageable);

    // Count activities by type
    long countByType(ActivityLog.ActivityType type);

    // Find activities by user and type
    List<ActivityLog> findByUserAndTypeOrderByDateDesc(String user, ActivityLog.ActivityType type, Pageable pageable);

    // ============= CLEANUP METHODS =============

    // Delete logs older than specified date
    @Modifying
    @Query("DELETE FROM ActivityLog a WHERE a.date < :cutoffDate")
    int deleteByDateBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Count logs older than specified date
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.date < :cutoffDate")
    long countByDateBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find oldest log date for cleanup statistics
    @Query("SELECT MIN(a.date) FROM ActivityLog a")
    LocalDateTime findOldestLogDate();

    // Delete logs by type older than specified date
    @Modifying
    @Query("DELETE FROM ActivityLog a WHERE a.date < :cutoffDate AND a.type = :type")
    int deleteByDateBeforeAndType(@Param("cutoffDate") LocalDateTime cutoffDate,
                                  @Param("type") ActivityLog.ActivityType type);
}