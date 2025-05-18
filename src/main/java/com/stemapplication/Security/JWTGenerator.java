package com.stemapplication.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; // For newer versions of JJWT
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTGenerator {

    // It's better to use a key derived from the secret string for HS512
//    private static final SecretKey key = Keys.hmacShaKeyFor(SecurityConstant.JWT_SECRET.getBytes());

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + SecurityConstant.JWT_EXPIRATION);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles) // Add roles to the token
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SECRET) // Use the derived key
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SecurityConstant.JWT_SECRET) // Use the derived key
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


//    This method parses a JWT (JSON Web Token) and extracts its claims (payload/data).
//
//    It uses the same key that was used to sign the token to verify its authenticity.
//    ✅ Where It’s Typically Used
//    This method is commonly used in a JWT authentication filter or service to:
//
//    Validate the token.
//
//    Extract user data (like username or roles) from the token.
//
//    Authenticate the user in the Spring Security context.



    public Claims getClaimsFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SecurityConstant.JWT_SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SecurityConstant.JWT_SECRET).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            // Log the exception for debugging
            System.err.println("JWT validation error: " + ex.getMessage());
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect", ex);
        }
    }
}