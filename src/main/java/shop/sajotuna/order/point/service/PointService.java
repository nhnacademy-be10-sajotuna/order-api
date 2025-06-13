package shop.sajotuna.order.point.service;


import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.PointPolicyType;

import java.util.List;

public interface PointService {
    List<PointHistoryResponse> getPointsByUserId(Long userId);

    PointHistoryResponse earnPointsForPurchase(Long userId, int totalPrice);

    PointHistoryResponse redeemPoints(Long userId, int pointAmount);

    PointHistoryResponse earnPointsByType(Long userId, PointPolicyType type);
}
