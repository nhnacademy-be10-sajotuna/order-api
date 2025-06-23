package shop.sajotuna.order.point.service;

import shop.sajotuna.order.point.controller.request.PointEvent;

public interface PointQueueService {
    void sendEarnPointsMessage(PointEvent event);
}
