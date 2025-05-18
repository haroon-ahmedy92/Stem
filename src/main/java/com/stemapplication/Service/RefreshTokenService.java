package com.stemapplication.Service;

import com.stemapplication.Models.RefreshToken;
import com.stemapplication.Models.UserEntity;
import com.stemapplication.Repository.RefreshTokenRepository;
import com.stemapplication.Repository.UserRepository;
import com.stemapplication.Security.SecurityConstant; // Assuming SecurityConstant holds expiry
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext // <--- This annotation is crucial for injection
    private EntityManager entityManager;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for refresh token creation: " + userId));

        // Delete existing token for the user if any
        refreshTokenRepository.deleteByUser(user); // Delete the old token


        entityManager.flush(); // Explicitly force pending changes (the delete) to the database

        // ... create new token ...
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(SecurityConstant.JWT_REFRESH_EXPIRATION))
                .build();

        return refreshTokenRepository.save(refreshToken); // Save the new token
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request.");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for refresh token deletion: " + userId));
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }
}