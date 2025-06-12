package shop.sajotuna.order.common.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends ApiException {
    private static final String MESSAGE = "Order not found.";

    public OrderNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
