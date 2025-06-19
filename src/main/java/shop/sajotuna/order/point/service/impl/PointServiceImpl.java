package shop.sajotuna.order.point.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.rabbitmq.PointRabbitProperties;
import shop.sajotuna.order.point.controller.request.PointEarnRequest;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.*;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.point.service.PointPolicyService;
import shop.sajotuna.order.point.service.PointQueueService;
import shop.sajotuna.order.point.service.PointService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicyService pointPolicyService;
    private final UserPointRepository userPointRepository;
    private final PointQueueService pointQueueService;

    @Override
    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointsByUserId(Long userId) {
        return pointHistoryRepository.getPointHistoriesByUserId(userId).stream()
                .map(PointHistoryResponse::from)
                .toList();
    }

    @Override
    public void earnPointsForPurchase(Long userId, int totalPrice) {
        PointPolicy pointPolicy = pointPolicyService.getPointPolicy(PointPolicyType.PURCHASE);

        pointQueueService.sendEarnPointsMessage(
                new PointEarnRequest(userId, pointPolicy.calculatePoint(totalPrice)));
    }

    @Override
    public void earnPointsByType(Long userId, PointPolicyType type) {
        UserPoint userPoint;
        if (type == PointPolicyType.REGISTER) {
            userPoint = UserPoint.create(userId);
            userPointRepository.save(userPoint);
        }

        PointPolicy pointPolicy = pointPolicyService.getPointPolicy(type);

        pointQueueService.sendEarnPointsMessage(
                new PointEarnRequest(userId, pointPolicy.getFixedPoint()));
    }

    // 반품 시 결제 금액을 포인트로 적립
    @Override
    public void earnPointsByReturned(PointEarnRequest request) {
        pointQueueService.sendEarnPointsMessage(request);
    }

    @Override
    public PointHistoryResponse redeemPoints(Long userId, int pointAmount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);

        userPoint.redeemPoint(pointAmount);
        PointHistory pointHistory = pointHistoryRepository.save(PointHistory.createRedeemHistory(userId, pointAmount));
        return PointHistoryResponse.from(pointHistory);
    }
}
