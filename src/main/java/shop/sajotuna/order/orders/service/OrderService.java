package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.coupon.service.UserCouponService;
import shop.sajotuna.order.orders.dto.*;
import shop.sajotuna.order.orders.entity.*;
import shop.sajotuna.order.orders.repository.*;
import shop.sajotuna.order.payment.entity.Payment;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.controller.request.PointEarnRequest;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.OrderNotFoundException;
import shop.sajotuna.order.point.service.PointService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final PaymentRepository paymentRepository;
    private final PointService pointService;
    private final UserCouponService userCouponService;
    private final OrderProductService orderProductService;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    // 주문 조회
    public OrderDetailResponse findOrderDetail(long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);

        List<OrderProductResponse> orderProducts = orderProductService.findByOrderId(orderId);

        Payment payment = paymentRepository.getPaymentByOrder_Id(orderId);

        return OrderDetailResponse.from(order, orderProducts, payment);
    }

    // 회원의 주문 목록 조회
    public List<OrderResponse> findOrdersByUserId(long userId){
        return orderRepository.findByUserId(userId).stream().map(OrderResponse::from).toList();
    }

    // 회원 주문 저장 - 주문상품, 결제, 쿠폰, 포인트도 한번에 처리하도록 구현해야 함
    public OrderResponse createUserOrder(OrderRequest orderRequest, Long userId) {
        int totalPrice = orderRequest.getItems().stream()
                .mapToInt(item -> item.getQty() * item.getAmount())
                .sum();
        Order savedOrder = orderRepository.save(orderRequest.toEntity(userId, totalPrice));
        // 주문 상품 추가
        int packagingPrice = orderProductService.saveOrderProduct(orderRequest.getItems(), savedOrder);

        // 쿠폰 먼저 사용 후 포인트 사용
        if (orderRequest.getUsedUserCoupon() != null) {
            int couponDiscountAmount = userCouponService.useCoupon(orderRequest.getUsedUserCoupon(), totalPrice);
            totalPrice -= couponDiscountAmount;
        }

        if(orderRequest.getUsedPoint() > 0){
            totalPrice -= orderRequest.getUsedPoint();
            pointService.redeemPoints(userId, orderRequest.getUsedPoint());
        }

        // 결제 정보 저장
        int paymentPrice = totalPrice + packagingPrice + orderRequest.getDeliveryPrice();
        savedOrder.setTotalPrice(paymentPrice);
        Payment payment = new Payment(savedOrder, orderRequest.getMethod(), paymentPrice);
        paymentRepository.save(payment);

        // 포인트 적립
        rabbitTemplate.convertAndSend(exchangeName, routingKey, new PointEarnRequest(userId, paymentPrice, PointPolicyType.PURCHASE));

        return OrderResponse.from(savedOrder);
    }

    // 비회원 주문 저장
    public OrderResponse createGuestOrder(GuestOrderRequest guestOrderRequest){
        int totalPrice = guestOrderRequest.getItems().stream()
                .mapToInt(item -> item.getQty() * item.getAmount())
                .sum();
        Order savedOrder = orderRepository.save(guestOrderRequest.toEntity(totalPrice));
        guestOrderRepository.save(new GuestOrder(savedOrder, guestOrderRequest));

        int packagingPrice = orderProductService.saveOrderProduct(guestOrderRequest.getItems(), savedOrder);

        // 결제 비용
        int finalPrice = totalPrice + guestOrderRequest.getDeliveryPrice() + packagingPrice;

        // 결제 정보 저장
        Payment payment = new Payment(savedOrder, guestOrderRequest.getMethod(), finalPrice);
        paymentRepository.save(payment);

        return OrderResponse.from(savedOrder);
    }

}
