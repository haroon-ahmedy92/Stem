package com.stemapplication.Security;

public class SecurityConstant {
    public static final long JWT_EXPIRATION =  900000 //15 minutes

    ; // 15 minutes ( 15 minutes × 60 seconds/minute × 1000 milliseconds/second = 900,000 milliseconds)
    public static final long JWT_REFRESH_EXPIRATION = 86400000 * 7; // e.g., 1 day (in milliseconds)
    // Ensure your JWT_SECRET is strong and stored securely (e.g., environment variable)
    public static final String JWT_SECRET = "yourSuperSecretddddddddddddddddhyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyKeyForHS512algorithmNeedsToBeVeryLongAndComplex";
}