package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class InvalidUserIdException extends ApiException {

    private static final String MESSAGE = "Invalid user ID.";

    public InvalidUserIdException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
