package shop.sajotuna.order.orders.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.domain.OrderProduct;

@Getter
@Builder
public class OrderProductResponse {
    private Long id;
    private String isbn;
    private OrderPackaging orderPackaging;
    private int qty;
    private int amount;
    private Boolean packagingRequest;

    public static OrderProductResponse from(OrderProduct product) {
        return OrderProductResponse.builder()
                .id(product.getId())
                .isbn(product.getIsbn())
                .orderPackaging(product.getOrderPackaging())
                .qty(product.getQty())
                .amount(product.getAmount().getAmount())
                .packagingRequest(product.getPackagingRequest())
                .build();
    }
}
