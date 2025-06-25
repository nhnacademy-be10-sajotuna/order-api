package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.dto.OrderProductRequest;
import shop.sajotuna.order.orders.dto.OrderRequest;
import shop.sajotuna.order.orders.entity.OrderPrice;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PackagingPricingService packagingPricingService;

    public OrderPrice calculatePrices(OrderRequest orderRequest) {
        int totalProductPrice = calculateTotalProductPrice(orderRequest.getItems());

        int packagingPrice = packagingPricingService.calculatePackagingPrice(orderRequest.getItems());

        return new OrderPrice(totalProductPrice, packagingPrice, orderRequest.getDeliveryPrice());
    }

    private int calculateTotalProductPrice(List<OrderProductRequest> orderProducts) {
        return orderProducts.stream()
                .mapToInt(item -> item.getAmount() * item.getQty())
                .sum();
    }
}
