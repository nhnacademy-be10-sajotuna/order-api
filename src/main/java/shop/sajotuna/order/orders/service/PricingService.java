package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.dto.OrderProductRequest;
import shop.sajotuna.order.orders.domain.OrderPrice;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PackagingPricingService packagingPricingService;

    public OrderPrice calculatePrices( List<OrderProductRequest> items, Money deliveryPrice) {
        Money totalProductPrice = calculateTotalProductPrice(items);
        Money packagingPrice = packagingPricingService.calculatePackagingPrice(items);

        return OrderPrice.create(
                totalProductPrice,
                packagingPrice,
                deliveryPrice
        );
    }

    private Money calculateTotalProductPrice(List<OrderProductRequest> orderProducts) {
        return orderProducts.stream()
                .map(item -> Money.of(item.getAmount()).multiply(item.getQty()))
                .reduce(Money.zero(), Money::plus);
    }
}
