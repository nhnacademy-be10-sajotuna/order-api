package shop.sajotuna.order.orders.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class InvalidStatusException extends ApiException {

    private static final String MESSAGE = "Invalid Order Status.";
    public InvalidStatusException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
