package com.stemapplication.DTO;

// Response DTO
public class SubscriptionResponseDto {

    private String message;
    private boolean success;

    // Constructor
    public SubscriptionResponseDto(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

