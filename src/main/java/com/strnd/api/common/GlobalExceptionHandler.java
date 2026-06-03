package com.strnd.api.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid 검증 실패 시 필드별 에러 메시지 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        // 필드명: 에러메시지 형태로 조합
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return errorBody(HttpStatus.BAD_REQUEST, message, request);
    }

    // 존재하지 않는 경로 요청
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(
            NoResourceFoundException e, HttpServletRequest request) {
        return errorBody(HttpStatus.NOT_FOUND, "요청한 경로를 찾을 수 없습니다.", request);
    }

    // 지원하지 않는 HTTP 메서드 요청
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        return errorBody(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식입니다.", request);
    }

    // ResponseStatusException (4xx/5xx) 커스텀 에러 형식 통일
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        // getReason()이 null인 경우 기본 메시지로 대체
        String message = (e.getReason() != null) ? e.getReason() : "요청을 처리할 수 없습니다.";
        return errorBody(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR, message, request);
    }

    // 에러 응답 바디 생성
    private ResponseEntity<Map<String, Object>> errorBody(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}