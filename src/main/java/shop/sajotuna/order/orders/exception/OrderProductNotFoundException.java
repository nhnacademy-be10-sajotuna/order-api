package shop.sajotuna.order.orders.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class OrderProductNotFoundException extends ApiException {
    private static final String MESSAGE = "OrderProduct not found.";

    public OrderProductNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
