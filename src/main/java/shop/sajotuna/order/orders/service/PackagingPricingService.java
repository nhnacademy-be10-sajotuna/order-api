package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.dto.OrderProductRequest;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackagingPricingService {
    private final OrderPackagingRepository orderPackagingRepository;

    public Money calculatePackagingPrice(List<OrderProductRequest> orderProductRequests) {
        return orderProductRequests.stream()
                .filter(OrderProductRequest::getPackagingRequest)
                .map(item -> {
                    OrderPackaging packaging = orderPackagingRepository
                            .findById(item.getOrderPackagingId())
                            .orElseThrow(PackageNotFoundException::new);
                    return packaging.getPrice();
                })
                .reduce(Money.zero(), Money::plus);
    }
}