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


//
//    @Transactional
//    public RefreshToken rotateRefreshToken(String oldToken, Long userId) {
//        // 1. Find and verify the old token
//        RefreshToken oldRefreshToken = findByToken(oldToken)
//                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
//
//        // 2. Verify it's not expired
//        verifyExpiration(oldRefreshToken);
//
//        // 3. Get the user
//        UserEntity user = oldRefreshToken.getUser();
//        if (user == null || !user.getId().equals(userId)) {
//            throw new RuntimeException("Token user mismatch");
//        }
//
//        // 4. Delete the old token atomically
//        refreshTokenRepository.delete(oldRefreshToken);
//        entityManager.flush(); // Force the delete to complete
//
//        // 5. Create the new token
//        RefreshToken newRefreshToken = RefreshToken.builder()
//                .user(user)
//                .token(UUID.randomUUID().toString())
//                .expiryDate(Instant.now().plusMillis(SecurityConstant.JWT_REFRESH_EXPIRATION))
//                .build();
//
//        return refreshTokenRepository.save(newRefreshToken);
//    }
//
//    // ✅ MODIFY createRefreshToken to NOT delete existing tokens
//    @Transactional
//    public RefreshToken createRefreshToken(Long userId) {
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found for refresh token creation: " + userId));
//
//        // ✅ REMOVE automatic deletion - let calling code handle it explicitly
//        // refreshTokenRepository.deleteByUser(user); // REMOVE THIS LINE
//
//        RefreshToken refreshToken = RefreshToken.builder()
//                .user(user)
//                .token(UUID.randomUUID().toString())
//                .expiryDate(Instant.now().plusMillis(SecurityConstant.JWT_REFRESH_EXPIRATION))
//                .build();
//
//        return refreshTokenRepository.save(refreshToken);
//    }





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
        System.out.println("deleteByToken called with token: >>" + token + "<<"); // Add this line
        Optional<RefreshToken> tokenToDelete = refreshTokenRepository.findByToken(token);
        if (tokenToDelete.isPresent()) {
            System.out.println("Token FOUND in DB for deletion."); // Add this line
            refreshTokenRepository.delete(tokenToDelete.get());
            System.out.println("Token deleted via repository."); // Add this line
        } else {
            System.out.println("Token NOT FOUND in DB for deletion."); // Add this line
        }
    }
}