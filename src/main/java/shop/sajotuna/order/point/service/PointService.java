package shop.sajotuna.order.point.service;


import shop.sajotuna.order.point.controller.response.PointHistoryResponse;

import java.util.List;

public interface PointService {
    List<PointHistoryResponse> getPointsByUserId(Long userId);

    PointHistoryResponse earnPointsForPurchase(Long userId, int totalPrice);

    PointHistoryResponse redeemPoints(Long userId, int pointAmount);

    PointHistoryResponse earnPointsForReview(Long userId);

    PointHistoryResponse earnPointsForRegistration(Long userId);
}
