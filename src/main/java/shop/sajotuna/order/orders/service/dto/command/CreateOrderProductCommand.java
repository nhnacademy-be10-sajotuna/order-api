package shop.sajotuna.order.orders.service.dto.command;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.controller.dto.request.OrderProductRequest;

import java.util.Set;

@Getter
@Builder
public class CreateOrderProductCommand {
    private final Long orderPackagingId;
    private final String isbn;
    private final Integer quantity;
    private final Money amount;
    private final Long bookCouponId;
    private final Boolean packagingRequest;
    private final Set<Long> categoryIds;

    public static CreateOrderProductCommand from(OrderProductRequest request) {
        return CreateOrderProductCommand.builder()
                .orderPackagingId(request.getOrderPackagingId())
                .isbn(request.getIsbn())
                .quantity(request.getQty())
                .amount(Money.of(request.getAmount()))
                .bookCouponId(request.getBookCouponId())
                .packagingRequest(request.getPackagingRequest())
                .categoryIds(request.getCategoryIds())
                .build();
    }

    public OrderProduct toEntity(OrderPackaging orderPackaging, UserCoupon coupon) {
        return OrderProduct.create(
                null,
                isbn,
                orderPackaging,
                quantity,
                amount,
                packagingRequest,
                coupon
        );
    }
}
