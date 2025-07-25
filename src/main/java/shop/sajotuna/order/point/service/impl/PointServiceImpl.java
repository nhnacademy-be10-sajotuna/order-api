package shop.sajotuna.order.point.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.PointHistory;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.domain.UserGrade;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.exception.UserGradeNotFoundException;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserGradeRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;
import shop.sajotuna.order.point.service.PointPolicyService;
import shop.sajotuna.order.point.service.PointService;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PointServiceImpl implements PointService {

    public static final String REDEEM_MESSAGE = "구매 사용";
    private static final String RETURN_MESSAGE = "환불로 인한 포인트 환수";
    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;
    private final PointPolicyService pointPolicyService;
    private final UserGradeRepository userGradeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PointHistoryResponse> getPointsByUserId(Long userId) {
        return pointHistoryRepository.getPointHistoriesByUserId(userId).stream()
                .map(PointHistoryResponse::from)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PointHistoryResponse> getPointsByUserId(Long userId, Pageable pageable) {
        return pointHistoryRepository.getPointHistoriesByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(PointHistoryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailablePointByUserId(Long userId) {
        UserPoint userPoint = userPointRepository.findByUserId(userId).orElseThrow(UserPointNotFoundException::new);

        return userPoint.getRemainPoint().getAmount();
    }

    @Override
    public PointHistoryResponse redeemPoints(Long userId, Money pointAmount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);

        userPoint.redeemPoint(pointAmount);
        PointHistory pointHistory = pointHistoryRepository.save(PointHistory.createRedeemHistory(userId, pointAmount, REDEEM_MESSAGE));
        return PointHistoryResponse.from(pointHistory);
    }

    @Override
    public void returnPoints(Long userId, Money pointAmount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(UserPointNotFoundException::new);

        userPoint.redeemPoint(pointAmount);
        pointHistoryRepository.save(PointHistory.createRedeemHistory(userId, pointAmount, RETURN_MESSAGE));
    }

    @Override
    public PointEarnRequest earnPoints(Long userId, PointPolicyType type, Money pointAmount) {
        Money amount = pointPolicyService.getPointPolicy(type).calculatePoint(pointAmount);

        UserGrade userGrade = userGradeRepository.findByUserId(userId).orElseThrow(UserGradeNotFoundException::new);
        Money gradePoint = userGrade.getGrade().calculatePoint(pointAmount);

        amount = amount.plus(gradePoint);

        return new PointEarnRequest(userId, type, amount);
    }

}
