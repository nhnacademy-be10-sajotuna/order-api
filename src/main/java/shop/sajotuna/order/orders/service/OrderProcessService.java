package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.orders.dto.OrderRequest;
import shop.sajotuna.order.orders.dto.OrderResponse;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.payment.service.PaymentService;
import shop.sajotuna.order.point.service.dto.event.PointEvent;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.PointQueueService;

@Service
@RequiredArgsConstructor
public class OrderProcessService {

    private final OrderProductCreateService orderProductCreateService;
    private final PaymentService paymentService;
    private final PointQueueService pointQueueService;
    private final OrderCreateService orderCreateService;

    @Transactional
    public OrderResponse processUserOrder(OrderRequest orderRequest, Long userId) {
        Order order = orderCreateService.processOrderCreation(orderRequest, userId);
        orderProductCreateService.saveOrderProducts(orderRequest.getItems(), order);

        paymentService.processUserPayment(order, orderRequest.getMethod(), userId);

        pointQueueService.sendEarnPointsMessage(new PointEvent(userId, PointPolicyType.PURCHASE, order.getFinalPrice()));
        return OrderResponse.from(order);
    }
}
