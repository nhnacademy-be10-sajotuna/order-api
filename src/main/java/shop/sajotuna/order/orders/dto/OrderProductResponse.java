package shop.sajotuna.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.orders.entity.OrderPackaging;
import shop.sajotuna.order.orders.entity.OrderProduct;
import shop.sajotuna.order.orders.entity.OrderStatus;

@Data
@AllArgsConstructor
public class OrderProductResponse {
    private Long id;
    private String isbn;
    private OrderPackaging orderPackaging;
    private int qty;
    private int amount;
    private OrderStatus status;
    private Boolean packagingRequest;

    public static OrderProductResponse from(OrderProduct product) {
        return new OrderProductResponse(product.getId(), product.getIsbn(), product.getOrderPackaging(), product.getQty(), product.getAmount(), product.getStatus(), product.getPackagingRequest());
    }
}
