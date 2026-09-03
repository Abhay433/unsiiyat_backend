package com.unsiiyat.backend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;

    private int status;

    private String error;

    private String message;

    private String path;

    private Map<String, String> validationErrors;

    private LocalDateTime timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(boolean success, int status, String error, String message, String path,
            Map<String, String> validationErrors, LocalDateTime timestamp) {
        this.success = success;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
        this.timestamp = timestamp;
    }

    public static ErrorResponse of(int status, String error, String message, String path) {

        return new ErrorResponse(false, status, error, message, path, null, LocalDateTime.now());
    }

    public static ErrorResponse validation(int status, String error, String message, String path,
            Map<String, String> validationErrors) {

        return new ErrorResponse(false, status, error, message, path, validationErrors, LocalDateTime.now());
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
