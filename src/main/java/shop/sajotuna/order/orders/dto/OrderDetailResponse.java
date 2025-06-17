package shop.sajotuna.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.payment.entity.Payment;
import shop.sajotuna.order.payment.entity.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderDetailResponse {
    private Long orderId;
    private LocalDateTime shippingDate;
    private LocalDateTime orderCreatedAt;
    private int totalPrice;
    private List<OrderProductResponse> items;
    private PaymentMethod method;
    private Integer amount;
    private LocalDateTime paymentCreatedAt;

    public static OrderDetailResponse from(Order order, List<OrderProductResponse> items, Payment payment) {
        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .shippingDate(order.getShippingDate())
                .orderCreatedAt(order.getCreatedAt())
                .totalPrice(order.getTotalPrice())
                .items(items)
                .method(payment.getMethod())
                .amount(payment.getAmount())
                .paymentCreatedAt(payment.getCreatedAt())
                .build();
    }
}
