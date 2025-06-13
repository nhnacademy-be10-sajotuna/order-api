package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class UserPointNotFoundException extends ApiException {

    private static final String MESSAGE = "User point not found.";

    public UserPointNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
