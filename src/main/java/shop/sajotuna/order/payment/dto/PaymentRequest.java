package shop.sajotuna.order.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.payment.entity.Payment;
import shop.sajotuna.order.payment.entity.PaymentMethod;

@Data
@AllArgsConstructor
public class PaymentRequest {
    private long orderId;
    private PaymentMethod method;
    private Integer amount;

    public Payment toEntity(Order order) {
        return new Payment(order, method, amount);
    }
}
