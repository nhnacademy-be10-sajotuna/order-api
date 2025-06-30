package shop.sajotuna.order.payment.service;

import shop.sajotuna.order.payment.dto.PaymentConfirmRequest;
import shop.sajotuna.order.payment.dto.PaymentResponse;

public interface ExternalPaymentService {
    PaymentResponse requestPaymentConfirm(PaymentConfirmRequest paymentConfirmRequest);
}
