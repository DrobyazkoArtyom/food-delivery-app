package ru.drobyazko.fooddeliveryservice.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.KitchenNotFoundException;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.MenuItemNotFoundException;
import ru.drobyazko.fooddeliveryservice.ordering.infrastructure.InvalidOrderException;
import ru.drobyazko.fooddeliveryservice.security.infrastructure.UserAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // TODO: should probably add logging to all these handlers
    @ExceptionHandler({KitchenNotFoundException.class, MenuItemNotFoundException.class})
    public ResponseEntity<String> handleResourceNotFoundExceptions() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({InvalidOrderException.class, JsonProcessingException.class, ValidationException.class})
    public ResponseEntity<String> handleResourceBadRequestExceptions() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler({PermissionDeniedException.class,})
    public ResponseEntity<String> handleResourceForbiddenExceptions() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // TODO: should pass along exception/message in the response entity. http status by itself is not enough
    @ExceptionHandler({UserAlreadyExistsException.class, DataIntegrityViolationException.class})
    public ResponseEntity<String> handleResourceConflictExceptions() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
