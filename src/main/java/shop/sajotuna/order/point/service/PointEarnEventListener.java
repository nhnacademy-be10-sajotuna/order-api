package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.point.controller.request.PointEarnRequest;
import shop.sajotuna.order.point.domain.PointPolicyType;

@Service
@RequiredArgsConstructor
public class PointEarnEventListener {

    private final PointService pointService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void earnPoints(PointEarnRequest request) {
        if (request.getType() == PointPolicyType.PURCHASE) {
            pointService.earnPointsForPurchase(request.getUserId(), request.getTotalPrice());
            return;
        }
        pointService.earnPointsByType(request.getUserId(), request.getType());
    }
}
