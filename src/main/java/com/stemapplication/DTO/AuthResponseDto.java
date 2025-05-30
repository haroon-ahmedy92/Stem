package com.stemapplication.DTO;
import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String type = "Bearer ";

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}