package shop.sajotuna.order.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.payment.domain.PaymentMethod;

@Data
@AllArgsConstructor
public class PaymentConfirmRequest {
    private String orderNumber;
    private int amount;
    private String paymentKey;
    private PaymentMethod paymentMethod;
}
