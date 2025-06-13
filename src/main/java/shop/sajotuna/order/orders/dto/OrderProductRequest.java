package shop.sajotuna.order.orders.dto;

import lombok.Data;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.orders.entity.OrderPackaging;
import shop.sajotuna.order.orders.entity.OrderProduct;

@Data
public class OrderProductRequest {
    private long orderPackagingId;
    private String isbn;
    private int qty;
    private int amount;
    private Boolean packagingRequest;

    public OrderProduct toEntity(Order order, OrderPackaging orderPackaging){
        return new OrderProduct(isbn, qty, amount, packagingRequest, order, orderPackaging);
    }
}
