package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.domain.UserCouponType;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.coupon.service.UserCouponService;
import shop.sajotuna.order.orders.dto.*;
import shop.sajotuna.order.orders.entity.*;
import shop.sajotuna.order.orders.exception.AlreadyUsedCouponException;
import shop.sajotuna.order.orders.exception.CouponNotFoundException;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.*;
import shop.sajotuna.order.payment.entity.Payment;
import shop.sajotuna.order.payment.repository.PaymentRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;
import shop.sajotuna.order.point.service.PointService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final GuestOrderRepository guestOrderRepository;
    private final OrderPackagingRepository orderPackagingRepository;
    private final PaymentRepository paymentRepository;
    private final PointService pointService;
    private final UserCouponRepository userCouponRepository;
    private final UserCouponService userCouponService;

    // 주문 조회
    @Transactional(readOnly = true)
    public OrderResponse findOrder(long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);

        return OrderResponse.from(order);
    }

    // 회원의 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderResponse> findOrdersByUserId(long userId){
        return orderRepository.findByUserId(userId).stream().map(OrderResponse::from).toList();
    }

    // 회원 주문 저장 - 주문상품, 결제, 쿠폰, 포인트도 한번에 처리하도록 구현해야 함
    @Transactional
    public OrderResponse createUserOrder(OrderRequest orderRequest, Long userId) {
        Order savedOrder = orderRepository.save(orderRequest.toEntity(userId));
        // 주문 상품 추가
        for(OrderProductRequest item: orderRequest.getItems()){
            OrderPackaging packaging = null;

            if(item.getPackagingRequest()){
                packaging = orderPackagingRepository.findById(item.getOrderPackagingId()).orElseThrow(PackageNotFoundException::new);
            }
            orderProductRepository.save(item.toEntity(savedOrder, packaging));
        }
        // 결제 비용
        int finalPrice = orderRequest.getTotalPrice() + orderRequest.getDeliveryPrice();
        // 포인트 사용 시
        if(orderRequest.getUsedPoint() > 0){
            finalPrice -= orderRequest.getUsedPoint();
            // 포인트 사용 내역 저장
            pointService.redeemPoints(userId, orderRequest.getUsedPoint());
        }
        // 쿠폰 사용 시
        if(orderRequest.getUsedUserCoupon() != null){
            UserCoupon userCoupon = userCouponRepository.findById(orderRequest.getUsedUserCoupon()).orElseThrow(CouponNotFoundException::new);
            if(userCoupon.getType() == UserCouponType.USED || userCoupon.getType() == UserCouponType.EXPIRED){
                throw new AlreadyUsedCouponException();
            }
            Coupon coupon = userCoupon.getCoupon();

            if(coupon.getType() == CouponType.FIXED){
                finalPrice -= coupon.getDiscountAmount();
            } else {
                long discount = ((long) orderRequest.getTotalPrice() * coupon.getDiscountAmount()) / 100;

                if(discount > coupon.getMaxDiscountAmount()){
                    discount = coupon.getMaxDiscountAmount();
                }
                finalPrice -= (int) discount;
            }
            // 쿠폰 내역 사용으로 변경
            userCouponService.updateUserCoupon(orderRequest.getUsedUserCoupon(), UserCouponType.USED);
        }

        // 결제 정보 저장
        Payment payment = new Payment(savedOrder, orderRequest.getMethod(), finalPrice);
        paymentRepository.save(payment);

        // 포인트 적립
        pointService.earnPointsForPurchase(userId, orderRequest.getTotalPrice());

        return OrderResponse.from(savedOrder);
    }

    // 비회원 주문 저장
    @Transactional
    public OrderResponse createGuestOrder(GuestOrderRequest guestOrderRequest){
        Order savedOrder = orderRepository.save(guestOrderRequest.toEntity());
        // 주문 상품 추가
        for(OrderProductRequest item: guestOrderRequest.getItems()){
            OrderPackaging packaging = null;

            if(item.getPackagingRequest()){
                packaging = orderPackagingRepository.findById(item.getOrderPackagingId()).orElseThrow(PackageNotFoundException::new);
            }
            orderProductRepository.save(item.toEntity(savedOrder, packaging));
        }
        // guest order 저장
        guestOrderRepository.save(new GuestOrder(savedOrder, guestOrderRequest));

        // 결제 비용
        int finalPrice = guestOrderRequest.getTotalPrice() + guestOrderRequest.getDeliveryPrice();

        // 결제 정보 저장
        Payment payment = new Payment(savedOrder, guestOrderRequest.getMethod(), finalPrice);
        paymentRepository.save(payment);

        return OrderResponse.from(savedOrder);
    }

}
