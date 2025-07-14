package shop.sajotuna.order.point.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.dto.event.PointEvent;

import java.util.List;

public interface PointService {

    List<PointHistoryResponse> getPointsByUserId(Long userId);
    
    Page<PointHistoryResponse> getPointsByUserId(Long userId, Pageable pageable);

    Integer getAvailablePointByUserId(Long userId);

    PointHistoryResponse redeemPoints(Long userId, Money pointAmount);

    void returnPoints(Long userId, Money pointAmount);

    PointEvent earnPoints(Long userId, PointPolicyType purchase, Money pointAmount);
}
