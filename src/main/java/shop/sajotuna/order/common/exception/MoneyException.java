package shop.sajotuna.order.common.exception;

import org.springframework.http.HttpStatus;

public class MoneyException extends ApiException {
    
    public MoneyException(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }
    
    public static class InvalidAmountException extends MoneyException {
        public InvalidAmountException(String message) {
            super(message);
        }
    }
    
    public static class InsufficientAmountException extends MoneyException {
        public InsufficientAmountException(String message) {
            super(message);
        }
    }
    
    public static class InvalidOperandException extends MoneyException {
        public InvalidOperandException(String message) {
            super(message);
        }
    }
}