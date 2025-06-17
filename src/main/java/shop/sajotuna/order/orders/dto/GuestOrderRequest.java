package shop.sajotuna.order.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.payment.entity.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GuestOrderRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    @NotBlank @Email
    private String email;

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

    @NotNull
    private List<OrderProductRequest> items;

    public Order toEntity(int totalPrice) {
        return Order.builder()
                .isMember(false)
                .shippingDate(shippingDate)
                .streetAddress(streetAddress)
                .deliveryPrice(deliveryPrice)
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now()).build();
    }
}
