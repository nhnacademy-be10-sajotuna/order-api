package shop.sajotuna.order.orders.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.controller.dto.response.OrderProductResponse;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.exception.OrderProductNotFoundException;
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
    private final OrderRepository orderRepository;

    // 주문 상품 조회
    @Transactional(readOnly = true)
    public OrderProductResponse findById(Long id) {
        OrderProduct orderProduct = orderProductRepository.findById(id).orElseThrow(OrderProductNotFoundException::new);

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

    // 특정 주문 번호에 포함된 상품들 삭제
    @Transactional
    public void deleteByOrderId(Long orderId){
        if(!orderRepository.existsById(orderId)){
            throw new OrderNotFoundException();
        }
        orderProductRepository.deleteByOrder_Id(orderId);
    }

    @Transactional(readOnly = true)
    public boolean isEligibleForReview(Long userId, String isbn) {
        if (userId == null || isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        return orderProductRepository.existsByOrderOrdererUserIdAndIsbnAndOrderStatus(userId, isbn, OrderStatus.DELIVERED);
    }
}
