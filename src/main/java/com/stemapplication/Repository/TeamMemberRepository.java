package com.stemapplication.Repository;

import com.stemapplication.Models.TeamMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    
    /**
     * Search team members by name, role, or qualification
     * Case-insensitive search across multiple fields
     */
    @Query("SELECT tm FROM TeamMember tm WHERE " +
           "LOWER(tm.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tm.role) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tm.qualification) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<TeamMember> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find team members by role (exact match, case-insensitive)
     */
    Page<TeamMember> findByRoleIgnoreCase(String role, Pageable pageable);
    
    /**
     * Check if a team member exists by email
     */
    boolean existsByEmailIgnoreCase(String email);
    
    /**
     * Find team member by email (case-insensitive)
     */
    TeamMember findByEmailIgnoreCase(String email);
}
