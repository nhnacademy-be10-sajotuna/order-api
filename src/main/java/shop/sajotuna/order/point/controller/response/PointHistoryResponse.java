package shop.sajotuna.order.point.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.sajotuna.order.point.domain.PointHistory;
import shop.sajotuna.order.point.domain.PointType;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PointHistoryResponse {
    private Long id;

    private Long userId;

    private int amount;

    private PointType type;

    private LocalDateTime createdAt;

    public static PointHistoryResponse from(PointHistory pointHistory) {
        return new PointHistoryResponse(
                pointHistory.getId(),
                pointHistory.getUserId(),
                pointHistory.getAmount(),
                pointHistory.getType(),
                pointHistory.getCreatedAt()
        );
    }
}
