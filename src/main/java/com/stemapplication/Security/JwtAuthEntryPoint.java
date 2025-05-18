package com.stemapplication.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.Logger; // Import logger
import org.slf4j.LoggerFactory; // Import logger factory


import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class); // Get a logger

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.error("Authentication error: {}", authException.getMessage(), authException); // Log the error with stack trace

        // Send the error response
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

    }
}