package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.domain.OrderPrice;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    public OrderPrice calculatePrices(List<OrderProduct> orderProducts, Money deliveryPrice) {
        Money totalProductPrice = orderProducts.stream()
                .map(OrderProduct::getTotalPrice)
                .reduce(Money.zero(), Money::plus);

        Money packagingPrice = orderProducts.stream()
                .map(OrderProduct::getPackagingPrice)
                .reduce(Money.zero(), Money::plus);

        return OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);
    }
}
