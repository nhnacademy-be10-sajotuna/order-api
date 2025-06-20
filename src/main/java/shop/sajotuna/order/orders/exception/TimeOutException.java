package shop.sajotuna.order.orders.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class TimeOutException extends ApiException {

    private static final String MESSAGE = "Time out.";

    public TimeOutException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}