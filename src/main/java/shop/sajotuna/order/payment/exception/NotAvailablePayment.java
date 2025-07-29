package shop.sajotuna.order.payment.exception;

import org.springframework.http.HttpStatus;
import shop.sajotuna.order.common.exception.ApiException;
import shop.sajotuna.order.payment.domain.PaymentMethod;

public class NotAvailablePayment extends ApiException {

    public static final String MESSAGE = "지원하지 않는 결제 수단입니다: %s";

    public NotAvailablePayment(PaymentMethod method) {
        super(HttpStatus.NOT_FOUND.value(), String.format(MESSAGE, method.name()));
    }
}
