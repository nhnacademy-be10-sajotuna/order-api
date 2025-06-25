package shop.sajotuna.order.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.orders.entity.OrderStatus;
import shop.sajotuna.order.payment.entity.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRequest {
    @NotNull
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime shippingDate;

    @NotBlank
    private String streetAddress;

    @NotNull
    @PositiveOrZero
    private int deliveryPrice;

    @NotNull
    private PaymentMethod method;

    private Long orderCouponId;

    @PositiveOrZero
    private int usedPoint;

    @NotNull
    private List<OrderProductRequest> items;

    public Order toEntity(Long userId) {
        return Order.createBaseUserOrder(shippingDate, streetAddress, userId);
    }
}
