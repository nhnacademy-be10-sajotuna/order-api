package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class InvalidPointPolicyValueException extends ApiException {
    private static final String MESSAGE = "Point policy value must be greater than 0.";

    public InvalidPointPolicyValueException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
