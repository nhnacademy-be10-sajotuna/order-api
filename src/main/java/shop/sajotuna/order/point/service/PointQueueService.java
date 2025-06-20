package shop.sajotuna.order.point.service;

import shop.sajotuna.order.point.controller.request.PointEarnRequest;

public interface PointQueueService {
    void sendEarnPointsMessage(PointEarnRequest request);
}
