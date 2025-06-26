package shop.sajotuna.order.point.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.*;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.point.service.PointService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointServiceImpl implements PointService {

    public static final String REDEEM_MESSAGE = "구매 사용";
    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointsByUserId(Long userId) {
        return pointHistoryRepository.getPointHistoriesByUserId(userId).stream()
                .map(PointHistoryResponse::from)
                .toList();
    }

    @Override
    public PointHistoryResponse redeemPoints(Long userId, Money pointAmount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);

        userPoint.redeemPoint(pointAmount);
        PointHistory pointHistory = pointHistoryRepository.save(PointHistory.createRedeemHistory(userId, pointAmount, REDEEM_MESSAGE));
        return PointHistoryResponse.from(pointHistory);
    }
}
