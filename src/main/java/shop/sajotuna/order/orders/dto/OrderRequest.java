package shop.sajotuna.order.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRequest {
    private Integer userId;

    private Boolean isMember;

    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime shippingDate;

    private String streetAddress;

    private String detailedAddress;

    private int deliveryPrice;

    private int totalPrice;

    private List<OrderProductRequest> items;
}
