package shop.sajotuna.order.orders.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.point.exception.OrderNotFoundException;
import shop.sajotuna.order.orders.dto.OrderProductResponse;
import shop.sajotuna.order.orders.dto.OrderProductUpdateRequest;
import shop.sajotuna.order.orders.entity.OrderProduct;
import shop.sajotuna.order.orders.repository.OrderProductRepository;
import shop.sajotuna.order.orders.repository.OrderRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;

    // 주문 상품 조회
    public OrderProductResponse findById(Long id) {
        OrderProduct orderProduct = orderProductRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return OrderProductResponse.from(Objects.requireNonNull(orderProduct));
    }

    // 주문 번호에 포함된 상품들 조회
    public List<OrderProductResponse> findByOrderId(Long orderId){
        if(!orderRepository.existsById(orderId)){
            throw new OrderNotFoundException();
        }
        List<OrderProduct> orderProducts = orderProductRepository.getOrderProductsByOrder_Id(orderId);

        return orderProducts.stream().map(OrderProductResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public void updateOrderProduct(Long id, OrderProductUpdateRequest request){
        OrderProduct orderProduct = orderProductRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Objects.requireNonNull(orderProduct).setStatus(request.getStatus());
    }

}
