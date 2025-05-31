package com.stemapplication.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enables @PreAuthorize, @PostAuthorize
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint, CustomUserDetailsService customUserDetailsService) {
        this.authEntryPoint = authEntryPoint;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // --- Public Auth Endpoints ---
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/refresh-token").permitAll()
                        .requestMatchers("/api/subscribe").permitAll()

                        // --- Authenticated User Profile Endpoints ---
                        .requestMatchers(HttpMethod.GET, "/api/auth/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/auth/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/users/me/change-password").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

                        // --- Admin & Super Admin Endpoints (Control Panel) ---
                        // Specific endpoints for admin panel actions
                        .requestMatchers("/api/admin/approve-user").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                        .requestMatchers("/api/admin/suspend-user").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                        .requestMatchers("/api/admin/promote-to-admin/**").hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers("/api/admin/demote-from-admin/**").hasAuthority("ROLE_SUPER_ADMIN")
                        // General user/admin management by Super Admin
                        .requestMatchers(HttpMethod.GET, "/api/admin/users").hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/admins").hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/users/{userId}").hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/users/{userId}").hasAuthority("ROLE_SUPER_ADMIN")

                        // --- Blog Public Endpoints (accessible to anyone) ---
                        .requestMatchers(HttpMethod.GET, "/api/blog").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/posts/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/posts/category/{categoryId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/featured").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/blog/popular").permitAll()

                        // --- Blog Authenticated Endpoints (for authors and admins) ---
                        .requestMatchers(HttpMethod.POST, "/api/blog/posts").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/blog/posts/{id}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/blog/posts/{id}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN")

                        // --- Comments Public Endpoints ---
                        .requestMatchers(HttpMethod.GET, "/api/comments/post/{blogPostId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/comments/post/{blogPostId}").permitAll()

                        // --- Comments Admin/Moderator Endpoints ---
                        .requestMatchers(HttpMethod.GET, "/api/admin/comments/pending").hasAnyAuthority("ROLE_USER","ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/comments/approve/{commentId}").hasAnyAuthority("ROLE_USER","ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/comments/delete/{commentId}").hasAnyAuthority("ROLE_USER","ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/comments/post/{blogPostId}").hasAnyAuthority("ROLE_USER","ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                        
                        // --- Gallery Public Endpoints ---
                        .requestMatchers(HttpMethod.GET, "/api/gallery").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gallery/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gallery/category/{categoryId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gallery/user/{userId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gallery/tag/{tag}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/gallery/featured").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/gallery/{id}/view").permitAll()
                        
                        // --- Gallery Authenticated Endpoints ---
                        .requestMatchers(HttpMethod.POST, "/api/gallery").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/gallery/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/gallery/{id}").authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }
}