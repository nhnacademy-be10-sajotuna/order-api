package shop.sajotuna.order.payment.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class PaymentNotFoundException extends ApiException {
    private static final String MESSAGE = "Payment not found.";

    public PaymentNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE);
    }
}
