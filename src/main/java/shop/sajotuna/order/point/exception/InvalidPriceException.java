package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class InvalidPriceException extends ApiException {

    private static final String MESSAGE = "가격은 항상 양수입니다: %d";
    public InvalidPriceException(int price) {
        super(HttpStatus.BAD_REQUEST.value(), String.format(MESSAGE, price));
    }
}
