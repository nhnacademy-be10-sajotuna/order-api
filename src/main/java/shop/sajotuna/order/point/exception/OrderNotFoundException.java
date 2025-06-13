package shop.sajotuna.order.point.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class OrderNotFoundException extends ApiException {
    private static final String MESSAGE = "Order not found.";

    public OrderNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
