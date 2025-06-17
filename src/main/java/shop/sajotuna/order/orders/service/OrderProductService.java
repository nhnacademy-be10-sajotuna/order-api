package shop.sajotuna.order.orders.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.dto.OrderProductRequest;
import shop.sajotuna.order.orders.dto.OrderProductResponse;
import shop.sajotuna.order.orders.dto.OrderProductUpdateRequest;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.orders.entity.OrderPackaging;
import shop.sajotuna.order.orders.entity.OrderProduct;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import shop.sajotuna.order.orders.repository.OrderProductRepository;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;
    private final OrderPackagingRepository orderPackagingRepository;
    private final OrderRepository orderRepository;

    // 주문 상품 조회
    @Transactional(readOnly = true)
    public OrderProductResponse findById(Long id) {
        OrderProduct orderProduct = orderProductRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return OrderProductResponse.from(Objects.requireNonNull(orderProduct));
    }

    // 주문 번호에 포함된 상품들 조회
    @Transactional(readOnly = true)
    public List<OrderProductResponse> findByOrderId(Long orderId){
        if(!orderRepository.existsById(orderId)){
            throw new OrderNotFoundException();
        }
        List<OrderProduct> orderProducts = orderProductRepository.getOrderProductsByOrder_Id(orderId);

        return orderProducts.stream().map(OrderProductResponse::from).collect(Collectors.toList());
    }

    // 주문 상품 배송 상태 업데이트
    @Transactional
    public void updateOrderProduct(Long id, OrderProductUpdateRequest request){
        OrderProduct orderProduct = orderProductRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Objects.requireNonNull(orderProduct).setStatus(request.getStatus());
    }

    // 주문 상품 저장
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
