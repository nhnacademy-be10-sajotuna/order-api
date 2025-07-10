package shop.sajotuna.order.payment.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;

public class PaymentFailException extends ApiException {
    private static final String MESSAGE = "Payment failed";

    public PaymentFailException() {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE);
    }
}
