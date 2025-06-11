package shop.sajotuna.order.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRequest {
    private long userId;

    private Boolean isMember;

    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime shippingDate;

    private String streetAddress;

    private int deliveryPrice;

    private int totalPrice;

    private List<OrderProductRequest> items;
}
