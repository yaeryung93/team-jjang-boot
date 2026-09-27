package com.teamjjang.common;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;
    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    public HttpStatus status() { return status; }
    public static ApiException notFound(String resource) {
        return new ApiException(HttpStatus.NOT_FOUND, resource + "을(를) 찾을 수 없습니다.");
    }
    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }
}
