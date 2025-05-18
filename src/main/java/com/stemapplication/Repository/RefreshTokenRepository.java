package com.stemapplication.Repository;

import com.stemapplication.Models.RefreshToken;
import com.stemapplication.Models.UserEntity; // Make sure UserEntity is imported
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(UserEntity user); // Existing method

    // Add this method: Spring Data JPA will generate the DELETE query
    @Transactional // Ensure this delete operation runs within the transaction
    void deleteByUser(UserEntity user);

    // Or delete by user ID directly
    @Transactional
    void deleteByUserId(Long userId);
}