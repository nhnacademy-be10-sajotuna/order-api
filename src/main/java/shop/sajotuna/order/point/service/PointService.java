package shop.sajotuna.order.point.service;

import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;

import java.util.List;

public interface PointService {

    List<PointHistoryResponse> getPointsByUserId(Long userId);

    PointHistoryResponse redeemPoints(Long userId, Money pointAmount);
}
