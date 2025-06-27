package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.PointHistory;
import shop.sajotuna.order.point.repository.PointHistoryRepository;

@Service
@RequiredArgsConstructor
public class PointHistoryWriter {

    public static final String REDEEM_MESSAGE = "구매 사용";

    private final PointHistoryRepository pointHistoryRepository;

    public void savePointEarnHistory(Long userId, Money pointAmount, String description) {
        pointHistoryRepository.save(PointHistory.createEarnHistory(userId, pointAmount, description));
    }

    public void savePointRedeemHistory(Long userId, Money pointAmount) {
        pointHistoryRepository.save(PointHistory.createRedeemHistory(userId, pointAmount, REDEEM_MESSAGE));
    }
}
