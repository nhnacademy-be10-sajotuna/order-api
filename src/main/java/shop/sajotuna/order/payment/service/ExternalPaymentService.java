package shop.sajotuna.order.payment.service;

import shop.sajotuna.order.payment.domain.PaymentMethod;
import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;

public interface ExternalPaymentService {
    PaymentResponse requestPaymentConfirm(PaymentConfirmRequest paymentConfirmRequest);

    boolean support(PaymentMethod paymentMethod);
}
