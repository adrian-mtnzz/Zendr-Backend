package com.zendr.backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Intercepta las excepciones de validación de entrada de los controladores y devuelve codigo 400 Bad Request
    // en lugar de 500 Internal Server Error. También muestra en la respuesta los mensajes de la excepcion.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
                );
        
        return ResponseEntity.badRequest().body(errors);
    }
    
    // Intercepta las excepciones IllegalArgumentException de los controladores y devuelve codigo 400 Bad Request
    // en lugar de 500 Internal Server Error. También muestra en la respuesta los mensajes de la excepcion.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}