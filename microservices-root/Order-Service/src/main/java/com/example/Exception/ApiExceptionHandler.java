package com.example.Exception;

import com.example.Exception.Payload.ExceptionMessage;
import com.example.Exception.Wrapper.CartNotFoundException;
import com.example.Exception.Wrapper.JwtAuthenticationException;
import com.example.Exception.Wrapper.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            JwtAuthenticationException.class
    })
    public <T extends BindException> ResponseEntity<ExceptionMessage> handleValidationException(final T ex) {
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<> (
                ExceptionMessage.builder()
                        .message("*" + Objects
                                .requireNonNull(ex.getBindingResult().getFieldError())
                                .getDefaultMessage() + "!**")
                        .httpStatus(badRequest)
                        .timestamp(ZonedDateTime
                                .now(ZoneId.systemDefault()))
                        .build(),
                badRequest
                );
    }

    @ExceptionHandler(value = {
            CartNotFoundException.class,
            OrderNotFoundException.class
    })
    public <T extends RuntimeException> ResponseEntity<ExceptionMessage> handleApiRequestException(final T e) {

        log.info("**ApiExceptionHandler controller, handle API request*\n");
        final var badRequest = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                ExceptionMessage.builder()
                        .message("#### " + e.getMessage() + "! ####")
                        .httpStatus(badRequest)
                        .timestamp(ZonedDateTime
                                .now(ZoneId.systemDefault()))
                        .build(),
                badRequest);
    }

    @ExceptionHandler(value = {
            IllegalStateException.class
    })
    public <T extends RuntimeException> ResponseEntity<ExceptionMessage> handleApiSaveDatabaseException(final T e) {
        log.info("**ApiExceptionHandler controller, handle API save database");

        return new ResponseEntity<>(ExceptionMessage.builder()
                .message("#### " + e.getMessage() + "! ####")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionMessage.builder()
                        .message("#### An unexpected error occurred: " + ex.getMessage()
                                + "! ####")
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
                        .build());
    }

}
