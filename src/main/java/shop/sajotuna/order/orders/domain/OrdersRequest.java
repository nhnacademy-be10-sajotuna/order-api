package shop.sajotuna.order.orders.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrdersRequest {
    private Boolean isMember;
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime shippingDate;
    private String streetAddress;
    private String detailedAddress;
    private int deliveryPrice;
    private int totalPrice;
}
