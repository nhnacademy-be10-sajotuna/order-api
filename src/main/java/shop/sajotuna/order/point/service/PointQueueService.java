package shop.sajotuna.order.point.service;

import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.dto.event.PointEvent;

public interface PointQueueService {
    void sendEarnPointsMessage(PointEvent event);

    void sendEarnPointsMessage(Long userId, PointPolicyType type, Money amount);
}
