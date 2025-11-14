package ru.drobyazko.fooddeliveryservice.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KitchenNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundExceptions() {
        return ResponseEntity.notFound().build();
    }

}
