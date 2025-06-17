package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.coupon.service.UserCouponService;
import shop.sajotuna.order.orders.dto.*;
import shop.sajotuna.order.orders.entity.*;
import shop.sajotuna.order.orders.exception.InvalidStatusException;
import shop.sajotuna.order.orders.repository.*;
import shop.sajotuna.order.payment.entity.Payment;
import shop.sajotuna.order.payment.repository.PaymentRepository;
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
        pointService.earnPointsForPurchase(userId, paymentPrice);

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

    // 주문 반품 처리
    @Transactional
    public void returnedOrder(Long userId, Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.setStatus(OrderStatus.RETURNED);

        // 반품시 결제금액은 포인트로 적립됨
        Payment payment = paymentRepository.getPaymentByOrder_Id(orderId);
        pointService.earnPointsForPurchase(userId, payment.getAmount());
    }

    // 주문 취소 처리
    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        if(!order.getStatus().equals(OrderStatus.PENDING)){
            throw new InvalidStatusException();
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

}
