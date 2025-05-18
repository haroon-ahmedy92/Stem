package com.stemapplication.Security;

public class SecurityConstant {
    public static final long JWT_EXPIRATION = 70000; // e.g., 70 seconds for testing, typically 15-60 minutes
    public static final long JWT_REFRESH_EXPIRATION = 86400000 * 7; // e.g., 1 day (in milliseconds)
    // Ensure your JWT_SECRET is strong and stored securely (e.g., environment variable)
    public static final String JWT_SECRET = "yourSuperSecretddddddddddddddddhyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyKeyForHS512algorithmNeedsToBeVeryLongAndComplex";
}