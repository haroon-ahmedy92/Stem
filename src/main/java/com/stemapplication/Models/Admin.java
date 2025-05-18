package com.stemapplication.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;
@Getter
@Setter
@Entity
@Table(name = "admins", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class Admin implements UserDetails {

    // Getters and Setters
    @Getter
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Setter
    @NotBlank @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @Getter
    @Setter
    @NotBlank @Size(min = 8)
    @Column(nullable = false)
    private String password;

    @Getter
    @Setter
    @NotBlank @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Setter
    @Getter
    @Size(max = 100)
    private String fullName;

    @Setter
    @Getter
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s./0-9]*$")
    private String phoneNumber;

    @Setter
    @Getter
    private LocalDate dateOfBirth;

    @Setter
    @Getter
    @Size(max = 100)
    private String occupation;

    @Setter
    @Getter
    @Size(max = 500)
    private String bio;

    @Setter
    @Getter
    @Size(max = 200)
    private String address;

    @Setter
    @Getter
    @Size(max = 100)
    private String education;

    @Setter
    @Getter
    @Size(max = 100)
    private String department;

    @Setter
    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "admin_roles", joinColumns = @JoinColumn(name = "admin_id"))
    private Set<String> roles = new HashSet<>();

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    // Constructors
    public Admin() {}
    public Admin(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void addRole(String role) { this.roles.add(role); }

    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
    @Override public boolean isAccountNonExpired() { return accountNonExpired; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    @Override public boolean isEnabled() { return enabled; }
}