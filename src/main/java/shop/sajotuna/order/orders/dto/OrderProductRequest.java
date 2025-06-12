package shop.sajotuna.order.orders.dto;

import lombok.Data;

@Data
public class OrderProductRequest {
    private long orderPackingId;
    private String isbn;
    private String productId;
    private int qty;
    private int amount;
    private Boolean packagingRequest;
}
