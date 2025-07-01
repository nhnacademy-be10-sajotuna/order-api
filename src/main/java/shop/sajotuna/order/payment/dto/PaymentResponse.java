package shop.sajotuna.order.payment.dto;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private PaymentMethod method;
    private Integer amount;
    private LocalDateTime createdAt;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .method(payment.getMethod())
                .amount(payment.getAmount().getAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
