package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.ReturnReason;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.payment.service.PaymentService;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.exception.InvalidUserIdException;
import shop.sajotuna.order.point.exception.OrderNotFoundException;
import shop.sajotuna.order.point.service.PointService;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentService paymentService;
    private final PointService pointService;
    private final RefundService refundService;

    // 주문 배송 중으로 변경
    public void shippedOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.shipped();
    }

    // 주문 반품 처리
    public void returnOrder(Long userId, Long orderId, ReturnReason returnReason) {
        Order order = orderRepository.findByIdWithOrderProducts(orderId).orElseThrow(OrderNotFoundException::new);
        order.returned(returnReason);

        // 반품시 결제금액은 포인트로 적립됨
        refundService.returnStock(order);

        refundService.returnCoupon(order);

        pointService.returnPoints(userId, order.getEarnedPoint());

        eventPublisher.publishEvent(
                new PointEarnRequest(
                        userId,
                        PointPolicyType.RETURNED,
                        order.getReturnPrice(returnReason)
                )
        );
    }

    // 주문 취소 처리
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);

        if(!Objects.equals(userId, order.getOrderer().getUserId())) {
            throw new InvalidUserIdException();
        }

        order.cancelled();
        // 주문에 사용한 쿠폰 되돌리기
        refundService.returnStock(order);

        refundService.returnCoupon(order);

        // 사용한 포인트 되돌리기 & 적립된 포인트 취소하기
        eventPublisher.publishEvent(
                new PointEarnRequest(
                        userId,
                        PointPolicyType.RETURN_USED_POINT,
                        order.getDiscounts().getUsedPoint()
                )
        );

        pointService.returnPoints(userId, order.getEarnedPoint());

        // 결제 취소 요청
        paymentService.cancelPayment(orderId, "cancel");
    }

    public void cancelOrderBeforePayment(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        order.cancelPayment();

        refundAndCleanup(order, userId);
    }

    public void refundAndCleanup(Order order, Long userId) {
        refundService.returnStock(order);

        if (userId == null) {
            orderRepository.delete(order);
            return;
        }

        refundService.returnCoupon(order);

        pointService.returnPoints(userId, order.getDiscounts().getEarnedPoint());

        eventPublisher.publishEvent(
                new PointEarnRequest(
                        order.getOrderer().getUserId(),
                        PointPolicyType.RETURNED,
                        order.getDiscounts().getUsedPoint()
                )
        );
    }
}
