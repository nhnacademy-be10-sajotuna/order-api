package shop.sajotuna.order.common.exception;

import org.springframework.http.HttpStatus;

public class NullValueException extends ApiException{

    public NullValueException(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }
}
