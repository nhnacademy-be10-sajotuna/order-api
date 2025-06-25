package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class InvalidPercentageException extends ApiException {

    private static final String MESSAGE = "Percentage cannot be greater than 100";

    public InvalidPercentageException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
