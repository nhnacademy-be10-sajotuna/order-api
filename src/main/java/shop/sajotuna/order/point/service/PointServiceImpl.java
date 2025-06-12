package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.*;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyService pointPolicyService;
    private final UserPointRepository userPointRepository;

    @Override
    public List<PointHistoryResponse> getPointsByUserId(Long userId) {
        return pointHistoryRepository.getPointHistoriesByUserId(userId).stream()
                .map(PointHistoryResponse::from)
                .toList();
    }

    @Override
    public PointHistoryResponse earnPointsForPurchase(Long userId, int totalPrice) {
        PointPolicy pointPolicy = pointPolicyService.getPointPolicy(PointPolicyType.PURCHASE);
        int earnedPoints = pointPolicy.calculatePoint(totalPrice);

        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);
        return getPointHistoryResponse(userId, userPoint, earnedPoints);
    }

    @Override
    public PointHistoryResponse redeemPoints(Long userId, int pointAmount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);

        userPoint.redeemPoint(pointAmount);
        PointHistory pointHistory = pointHistoryRepository.save(new PointHistory(userId, pointAmount, PointType.REDEEMED));
        return PointHistoryResponse.from(pointHistory);
    }

    @Override
    public PointHistoryResponse earnPointsByType(Long userId, PointPolicyType type) {
        PointPolicy pointPolicy = pointPolicyService.getPointPolicy(type);
        int earnedPoints = pointPolicy.getFixedPoint();

        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);
        return getPointHistoryResponse(userId, userPoint, earnedPoints);
    }

    private PointHistoryResponse getPointHistoryResponse(Long userId, UserPoint userPoint, int earnedPoints) {
        userPoint.earnPoint(earnedPoints);
        PointHistory pointHistory = pointHistoryRepository.save(new PointHistory(userId, earnedPoints, PointType.EARNED));
        return PointHistoryResponse.from(pointHistory);
    }
}
