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
    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointsByUserId(Long userId) {
        return pointHistoryRepository.getPointHistoriesByUserId(userId).stream()
                .map(PointHistoryResponse::from)
                .toList();
    }

    @Override
    public PointHistoryResponse earnPointsForPurchase(Long userId, int totalPrice) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);

        PointPolicy pointPolicy = pointPolicyService.getPointPolicy(PointPolicyType.PURCHASE);
        int earnedPoints = pointPolicy.calculatePoint(totalPrice);
        userPoint.earnPoint(earnedPoints);

        return getPointHistoryResponse(userId, earnedPoints);
    }

    @Override
    public PointHistoryResponse redeemPoints(Long userId, int pointAmount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);

        userPoint.redeemPoint(pointAmount);
        PointHistory pointHistory = pointHistoryRepository.save(PointHistory.createRedeemHistory(userId, pointAmount));
        return PointHistoryResponse.from(pointHistory);
    }

    @Override
    public PointHistoryResponse earnPointsByReview(Long userId, PointPolicyType type) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);

        PointPolicy pointPolicy = pointPolicyService.getPointPolicy(type);
        int earnedPoints = pointPolicy.getFixedPoint();
        userPoint.earnPoint(earnedPoints);

        return getPointHistoryResponse(userId, earnedPoints);
    }

    @Override
    public PointHistoryResponse earnPointsByRegister(Long userId) {
        UserPoint userPoint = UserPoint.create(userId);
        userPointRepository.save(userPoint);

        PointPolicy pointPolicy = pointPolicyService.getPointPolicy(PointPolicyType.REGISTER);
        int earnedPoints = pointPolicy.getFixedPoint();
        userPoint.earnPoint(earnedPoints);

        return getPointHistoryResponse(userId, earnedPoints);
    }

    private PointHistoryResponse getPointHistoryResponse(Long userId, int earnedPoints) {
        PointHistory pointHistory = pointHistoryRepository.save(PointHistory.createEarnHistory(userId, earnedPoints));
        return PointHistoryResponse.from(pointHistory);
    }
}
