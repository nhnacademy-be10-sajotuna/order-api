package shop.sajotuna.order.orders.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.sajotuna.order.orders.exception.NotFoundException;

@RestControllerAdvice
public class RestGlobalExceptionHandler {
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> notFoundException(Exception ex) {
        String message = ex.getMessage();

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({SecurityException.class})
    public ResponseEntity<String> securityException(Exception ex) {
        String message = ex.getMessage();

        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }
}
