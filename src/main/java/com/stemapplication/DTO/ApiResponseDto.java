package com.stemapplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto<T> {
    
    private boolean success;
    private T data;
    private String message;
    
    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(true, data, message);
    }
    
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, data, null);
    }
    
    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(false, null, message);
    }
}
