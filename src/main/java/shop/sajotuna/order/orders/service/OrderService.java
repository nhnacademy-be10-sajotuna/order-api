package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.controller.dto.response.OrderDetailResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderProductResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderResponse;
import shop.sajotuna.order.orders.domain.*;
import shop.sajotuna.order.orders.repository.*;
import shop.sajotuna.order.payment.domain.Payment;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.service.dto.event.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.OrderNotFoundException;
import shop.sajotuna.order.point.service.PointQueueService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderProductService orderProductService;
    private final PointQueueService pointQueueService;

    private final String SCHEDULE = "0 0 12 * * *";

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
    public Page<OrderResponse> findOrdersByUserId(long userId, Pageable pageable){
        return orderRepository.findOrdersByOrderer_UserId(userId, pageable).map(OrderResponse::from);
    }

    // 주문 상태에 따른 주문들 조회
    public Page<OrderResponse> findOrdersByStatus(OrderStatus orderStatus, Pageable pageable) {
        return orderRepository.findOrdersByStatus(orderStatus, pageable).map(OrderResponse::from);
    }

    // 주문 배송 중으로 변경
    @Transactional
    public void shippedOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.shipped();
    }

    // 배송중으로 변경된 주문은 일정 시간경과 후 완료 처리 됨
    @Scheduled(cron = SCHEDULE) // 매일 낮 12시 마다 실행됨
    @Transactional
    public void deliveredOrder(){
        // 현재 시간 기준으로 1일 이상 지난 주문들을 가져온다
        List<Order> orders = orderRepository.findShippedOrders(LocalDateTime.now().minusDays(1));
        // 배송 날짜와 1일 이상 차이가 난다면 배송완료로 변경
        orders.forEach(Order::delivered);
    }

    // 주문 반품 처리
    @Transactional
    public void returnedOrder(Long userId, Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.returned();

        // 반품시 결제금액은 포인트로 적립됨
        Payment payment = paymentRepository.getPaymentByOrder_Id(orderId);
        pointQueueService.sendEarnPointsMessage(new PointEvent(userId, PointPolicyType.RETURNED, payment.getAmount()));
    }

    // 주문 취소 처리
    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.cancelled();
    }

}
