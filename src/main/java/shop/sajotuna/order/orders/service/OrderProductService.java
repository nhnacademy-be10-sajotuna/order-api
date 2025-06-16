package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.orders.dto.OrderProductRequest;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.orders.entity.OrderPackaging;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import shop.sajotuna.order.orders.repository.OrderProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;
    private final OrderPackagingRepository orderPackagingRepository;


    public int saveOrderProduct(List<OrderProductRequest> orderProductRequest, Order order) {

        int packagingPrice = 0;
        for (OrderProductRequest item : orderProductRequest) {
            OrderPackaging packaging = null;

            if (item.getPackagingRequest()) {
                packaging = orderPackagingRepository.findById(item.getOrderPackagingId()).orElseThrow(PackageNotFoundException::new);
                packagingPrice += packaging.getPrice();
            }
            orderProductRepository.save(item.toEntity(order, packaging));
        }
        return packagingPrice;
    }
}
