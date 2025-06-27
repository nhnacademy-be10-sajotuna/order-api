package shop.sajotuna.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.domain.PaymentMethod;

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
    private OrderStatus status;
    private List<OrderProductResponse> items;
    private PaymentMethod method;
    private Integer amount;
    private LocalDateTime paymentCreatedAt;

    public static OrderDetailResponse from(Order order, List<OrderProductResponse> items, Payment payment) {
        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .shippingDate(order.getShippingDate())
                .orderCreatedAt(order.getCreatedAt())
                .totalPrice(order.getTotalPrice().getAmount())
                .status(order.getStatus())
                .items(items)
                .method(payment.getMethod())
                .amount(payment.getAmount().getAmount())
                .paymentCreatedAt(payment.getCreatedAt())
                .build();
    }
}
