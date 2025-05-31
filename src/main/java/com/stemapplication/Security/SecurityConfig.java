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
    private final CustomUserDetailsService customUserDetailsService; // Autowire your custom service

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
                        .requestMatchers("/api/admin/promote-to-admin/**").hasAuthority("ROLE_SUPER_ADMIN")
                        // General user/admin management by Super Admin
                        .requestMatchers(HttpMethod.GET, "/api/admin/users").hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/admins").hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/users/{userId}").hasAuthority("ROLE_SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/users/{userId}").hasAuthority("ROLE_SUPER_ADMIN")


                        // --- Blog Public Endpoints (accessible to anyone) ---
                        .requestMatchers(HttpMethod.GET, "/api/blog").permitAll() // from PublicPostController
                        .requestMatchers(HttpMethod.GET, "/api/blog/posts").permitAll() // from PublicPostController
                        .requestMatchers(HttpMethod.GET, "/api/blog/posts/{id}").permitAll() // from PublicPostController
                        .requestMatchers(HttpMethod.GET, "/api/blog/posts/category/{categoryId}").permitAll() // from PublicPostController
                        .requestMatchers(HttpMethod.GET, "/api/blog/categories").permitAll() // from PublicPostController
                        .requestMatchers(HttpMethod.GET, "/api/blog/featured").permitAll() // from PublicPostController
                        .requestMatchers(HttpMethod.GET, "/api/blog/popular").permitAll() // from PublicPostController

                        // --- Blog Authenticated Endpoints (for authors and admins) ---
                        // Note: The base path for these is now /api/blog/posts in AuthenticatedPostController
                        .requestMatchers(HttpMethod.POST, "/api/blog/posts").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN") // createPost
                        .requestMatchers(HttpMethod.PUT, "/api/blog/posts/{id}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN") // updatePost
                        .requestMatchers(HttpMethod.DELETE, "/api/blog/posts/{id}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN") // deletePost

                        // --- Comments Public Endpoints ---
                        .requestMatchers(HttpMethod.GET, "/api/comments/post/{blogPostId}").permitAll() // Get approved comments for a post
                        .requestMatchers(HttpMethod.POST, "/api/comments/post/{blogPostId}").permitAll() // Allow guests and authenticated to post comments

                        // --- Comments Admin/Moderator Endpoints ---
                        .requestMatchers(HttpMethod.GET, "/api/admin/comments/pending").hasAnyAuthority("ROLE_USER","ROLE_ADMIN", "ROLE_SUPER_ADMIN") // Changed path
                        .requestMatchers(HttpMethod.PUT, "/api/admin/comments/approve/{commentId}").hasAnyAuthority("ROLE_USER","ROLE_ADMIN", "ROLE_SUPER_ADMIN") // Changed path
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/comments/delete/{commentId}").hasAnyAuthority("ROLE_USER","ROLE_ADMIN", "ROLE_SUPER_ADMIN") // Changed path
                        .requestMatchers(HttpMethod.GET, "/api/admin/comments/post/{blogPostId}").hasAnyAuthority("ROLE_USER","ROLE_ADMIN", "ROLE_SUPER_ADMIN") // Already correct


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
        authProvider.setUserDetailsService(customUserDetailsService); // Use your custom user details service
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter(); // This filter should be a @Component or defined as a bean
    }
}