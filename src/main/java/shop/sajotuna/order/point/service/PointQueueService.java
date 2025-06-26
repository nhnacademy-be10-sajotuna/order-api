package shop.sajotuna.order.point.service;

import shop.sajotuna.order.point.service.dto.event.PointEvent;

public interface PointQueueService {
    void sendEarnPointsMessage(PointEvent event);
}
