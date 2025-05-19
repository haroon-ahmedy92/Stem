package com.stemapplication.Security;

public class SecurityConstant {
    public static final long JWT_EXPIRATION = 300000; // 5 minutes (300,000 milliseconds)
    public static final long JWT_REFRESH_EXPIRATION = 86400000 * 7; // e.g., 1 day (in milliseconds)
    // Ensure your JWT_SECRET is strong and stored securely (e.g., environment variable)
    public static final String JWT_SECRET = "yourSuperSecretddddddddddddddddhyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyKeyForHS512algorithmNeedsToBeVeryLongAndComplex";
}