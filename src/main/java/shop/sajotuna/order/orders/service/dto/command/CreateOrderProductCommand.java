package shop.sajotuna.order.orders.service.dto.command;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.dto.OrderProductRequest;

@Getter
@Builder
public class CreateOrderProductCommand {
    private final long orderPackagingId;
    private final String isbn;
    private final int quantity;
    private final Money amount;
    private final boolean packagingRequest;

    public static CreateOrderProductCommand from(OrderProductRequest request) {
        return CreateOrderProductCommand.builder()
                .orderPackagingId(request.getOrderPackagingId())
                .isbn(request.getIsbn())
                .quantity(request.getQty())
                .amount(Money.of(request.getAmount()))
                .packagingRequest(request.getPackagingRequest())
                .build();
    }

    public OrderProduct toEntity(OrderPackaging orderPackaging) {
        return OrderProduct.create(
                null,
                isbn,
                orderPackaging,
                quantity,
                amount,
                packagingRequest
        );
    }
}
