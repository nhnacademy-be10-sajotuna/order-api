package shop.sajotuna.order.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.payment.entity.Payment;
import shop.sajotuna.order.payment.entity.PaymentMethod;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private PaymentMethod method;
    private Integer amount;
    private LocalDateTime createdAt;

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getMethod(), payment.getAmount(), payment.getCreatedAt());
    }
}
