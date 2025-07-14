package shop.sajotuna.order.orders.service.process;

import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderCommand;

import java.util.List;

public interface OrderProcessor {
    Discounts processDiscounts(CreateOrderCommand command, List<OrderProduct> orderProducts);

    Money processPointEarn(CreateOrderCommand command, Order order);
}
