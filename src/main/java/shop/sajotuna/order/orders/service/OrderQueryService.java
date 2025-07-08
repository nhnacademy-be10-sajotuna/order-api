package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.controller.dto.response.OrderDetailResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderInfoResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderProductResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderResponse;
import shop.sajotuna.order.orders.domain.*;
import shop.sajotuna.order.orders.repository.*;
import shop.sajotuna.order.orders.service.product.OrderProductService;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderProductService orderProductService;

    public OrderInfoResponse getOrderInfo(String orderNumber){
        Order order = orderRepository.findOrderByOrderNumber(orderNumber);
        if(order == null){
            throw new OrderNotFoundException();
        }

        return OrderInfoResponse.from(order);
    }

    // 주문 조회
    public OrderDetailResponse findOrderDetail(long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);

        List<OrderProductResponse> orderProducts = orderProductService.findByOrderId(orderId);

        Payment payment = paymentRepository.getPaymentByOrder_Id(orderId);

        return OrderDetailResponse.from(order, orderProducts, payment);
    }

    // 비회원 주문 조회
    public OrderDetailResponse findOrderDetailByOrderNumber(String orderNumber){
        Order order = orderRepository.findOrderByOrderNumber(orderNumber);
        if(order == null){
            throw new OrderNotFoundException();
        }

        List<OrderProductResponse> orderProducts = orderProductService.findByOrderId(order.getId());
        Payment payment = paymentRepository.getPaymentByOrder_Id(order.getId());

        return OrderDetailResponse.from(order, orderProducts, payment);
    }

    public Page<OrderResponse> findAllOrders(Pageable pageable) {
        return orderRepository.findAllBy(pageable).map(OrderResponse::from);
    }

    // 회원의 주문 목록 조회
    public Page<OrderInfoResponse> findOrdersByUserId(long userId, Pageable pageable){
        return orderRepository.findOrdersByOrdererUserId(userId, pageable).map(OrderInfoResponse::from);
    }

    // 주문 상태에 따른 주문들 조회
    public Page<OrderResponse> findOrdersByStatus(OrderStatus orderStatus, Pageable pageable) {
        return orderRepository.findOrdersByStatus(orderStatus, pageable).map(OrderResponse::from);
    }
}
