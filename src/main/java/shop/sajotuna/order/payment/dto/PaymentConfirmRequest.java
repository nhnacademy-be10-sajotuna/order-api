package shop.sajotuna.order.payment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.payment.domain.PaymentMethod;

@Data
@AllArgsConstructor
public class PaymentConfirmRequest {
    @NotNull
    private PaymentMethod paymentMethod;

    @NotEmpty
    private String orderNumber;

    private int amount;

    private String paymentKey;
}
