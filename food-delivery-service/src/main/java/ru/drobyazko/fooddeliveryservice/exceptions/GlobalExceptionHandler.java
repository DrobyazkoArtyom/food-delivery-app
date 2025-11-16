package ru.drobyazko.fooddeliveryservice.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.KitchenNotFoundException;
import ru.drobyazko.fooddeliveryservice.catalogue.infrastructure.MenuItemNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({KitchenNotFoundException.class, MenuItemNotFoundException.class})
    public ResponseEntity<String> handleResourceNotFoundExceptions() {
        return ResponseEntity.notFound().build();
    }

}
