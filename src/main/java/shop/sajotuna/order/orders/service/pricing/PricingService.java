package shop.sajotuna.order.orders.service.pricing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.DeliveryPrice;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.domain.OrderPrice;
import shop.sajotuna.order.orders.repository.DeliveryPriceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final DeliveryPriceRepository deliveryPriceRepository;

    @Transactional(readOnly = true)
    public OrderPrice calculatePrices(List<OrderProduct> orderProducts) {
        Money totalProductPrice = orderProducts.stream()
                .map(OrderProduct::getTotalPrice)
                .reduce(Money.zero(), Money::plus);

        Money packagingPrice = orderProducts.stream()
                .map(OrderProduct::getPackagingPrice)
                .reduce(Money.zero(), Money::plus);

        DeliveryPrice deliveryPricePolicy = deliveryPriceRepository.getDefaultDeliveryPrice();
        Money deliveryPrice = deliveryPricePolicy.calculateDeliveryPrice(totalProductPrice);

        return OrderPrice.create(totalProductPrice, packagingPrice, deliveryPrice);
    }
}
