package shop.sajotuna.order.orders.service.dto.command;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Orderer;
import shop.sajotuna.order.orders.domain.ShippingInfo;
import shop.sajotuna.order.payment.domain.PaymentMethod;

import java.util.List;

@Getter
@Builder
public class CreateOrderCommand {
    private final Orderer orderer;
    private final ShippingInfo shippingInfo;
    private final PaymentMethod paymentMethod;
    private final Long orderCouponId;
    private final Money usedPoint;
    private final List<CreateOrderProductCommand> items;

    public Long getUserId() {
        return orderer.getUserId();
    }
}
