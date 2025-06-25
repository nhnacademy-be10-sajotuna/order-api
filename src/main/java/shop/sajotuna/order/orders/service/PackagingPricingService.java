package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.dto.OrderProductRequest;
import shop.sajotuna.order.orders.entity.OrderPackaging;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackagingPricingService {
    private final OrderPackagingRepository orderPackagingRepository;

    public int calculatePackagingPrice(List<OrderProductRequest> orderProductRequests) {
        return orderProductRequests.stream()
                .filter(OrderProductRequest::getPackagingRequest)
                .mapToInt(item -> {
                    OrderPackaging packaging = orderPackagingRepository
                            .findById(item.getOrderPackagingId())
                            .orElseThrow(PackageNotFoundException::new);
                    return packaging.getPrice();
                })
                .sum();
    }
}