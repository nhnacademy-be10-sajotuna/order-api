package shop.sajotuna.order.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import shop.sajotuna.order.orders.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GuestOrderRequest {
    private String name;

    private String phoneNumber;

    private String email;

    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime shippingDate;

    private String streetAddress;

    private int deliveryPrice;

    private int totalPrice;

    private List<OrderProductRequest> items;

    public Order toEntity(){
        return new Order(false, shippingDate, streetAddress, deliveryPrice, totalPrice, null);
    }
}
