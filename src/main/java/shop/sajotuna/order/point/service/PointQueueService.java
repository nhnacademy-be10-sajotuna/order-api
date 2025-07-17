package shop.sajotuna.order.point.service;

import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;

public interface PointQueueService {
    void sendEarnPointsMessage(PointEarnRequest event);
}
