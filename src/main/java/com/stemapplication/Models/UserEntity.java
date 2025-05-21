package com.stemapplication.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; // Import LocalDate for birthdate
import java.time.LocalDateTime; // Import LocalDateTime for lastLogin
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users") // Ensure your table name is correct
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Full name or display name

    @Column(unique = true, nullable = false)
    private String username; // Login identifier

    @Column(unique = true, nullable = false)
    private String email;    // Contact email

    @Column(nullable = false)
    private String password;

    private String department;

    @Column(nullable = false)
    private boolean approved = false; // Default to not approved

    // --- Additional Profile Fields ---

    private String phone; // e.g., +1234567890 (nullable)
    private String address; // e.g., Street, City, Zip, Country (nullable)
    @Column(columnDefinition = "TEXT") // Use TEXT for longer text
    private String bio; // e.g., User biography (nullable)
    private LocalDate birthdate; // e.g., 1990-05-15 (nullable)
    private String occupation; // e.g., Software Engineer (nullable)
    private String education; // e.g., Bachelor's Degree in Computer Science (nullable)
    private String profilePictureUrl; // e.g., URL to an image file (nullable)

    // --- Notification Settings Fields ---
    // Corresponding to the nested 'notifications' object
    private boolean notificationEmailEnabled = false; // Default to false
    private boolean notificationAppEnabled = false; // Default to false
    private boolean notificationUpdatesEnabled = false; // Default to false

    // --- Security Settings Fields ---
    // Corresponding to the nested 'security' object
    private boolean securityTwoFactorEnabled = false; // Default to false
    private String securitySessionTimeout; // e.g., "30m", "1h", null (nullable)

    // --- Authentication/Session Tracking Field ---
    private LocalDateTime lastLogin; // Timestamp of the last successful login (nullable)


    // --- Relationships ---
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<Role> roles = new ArrayList<>();

    // Note: Relationship to RefreshToken is often OneToOne or OneToMany
    // This relationship was not explicitly in your provided UserEntity
    // example, but you likely have it in RefreshToken. Ensure cascading
    // delete is handled for RefreshToken when UserEntity is deleted.
    // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // private RefreshToken refreshToken; // Example if OneToOne

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<UserRefreshToken> refreshTokens; // Example if OneToMany
    // (assuming RefreshToken is now UserRefreshToken or similar)


    // Custom constructor for essential fields if needed
    public UserEntity(String name, String username, String email, String password, String department, List<Role> roles) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.department = department;
        this.roles = roles;
        this.approved = false; // default
        // Other fields default to null/false
    }
}