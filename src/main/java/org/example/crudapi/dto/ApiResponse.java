package org.example.crudapi.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        int statusCode,
        T message,
        LocalDateTime timestamp
) {
    public ApiResponse(int statusCode, T message) {
        this(statusCode, message, LocalDateTime.now());
    }

    public record ExceptionMessage(String errorMessage) {}
}
