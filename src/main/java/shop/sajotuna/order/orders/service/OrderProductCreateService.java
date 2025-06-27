package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.dto.OrderProductRequest;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import shop.sajotuna.order.orders.repository.OrderProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProductCreateService {

    private final OrderProductRepository orderProductRepository;
    private final OrderPackagingRepository orderPackagingRepository;

    public void saveOrderProducts(List<OrderProductRequest> orderProductRequests, Order order) {
        List<OrderProduct> orderProducts = orderProductRequests.stream()
                .map(item -> createOrderProduct(item, order))
                .toList();
        orderProductRepository.saveAll(orderProducts);
    }

    private OrderProduct createOrderProduct(OrderProductRequest item, Order order) {
        if (item.getPackagingRequest()) {
            return item.toEntity(order, getPackaging(item.getOrderPackagingId()));
        }
        return item.toEntity(order, null);
    }

    private OrderPackaging getPackaging(Long packagingId) {
        return orderPackagingRepository.findById(packagingId)
                .orElseThrow(PackageNotFoundException::new);
    }
}
