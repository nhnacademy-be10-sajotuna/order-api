package shop.sajotuna.order.orders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.domain.OrderProduct;

@Data
public class OrderProductRequest {
    private long orderPackagingId;

    @NotBlank
    private String isbn;

    @NotNull
    @PositiveOrZero
    private int qty;

    @NotNull
    @PositiveOrZero
    private int amount;

    //TODO: 상품 쿠폰 추가

    @NotNull
    private Boolean packagingRequest;

    public OrderProduct toEntity(Order order, OrderPackaging orderPackaging){
        return OrderProduct.builder()
                .order(order)
                .orderPackaging(orderPackaging)
                .amount(amount)
                .qty(qty)
                .isbn(isbn)
                .packagingRequest(packagingRequest).build();
    }
}
