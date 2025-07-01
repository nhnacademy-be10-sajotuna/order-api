package shop.sajotuna.order.point.controller.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.point.domain.PointHistory;
import shop.sajotuna.order.point.domain.PointHistoryType;

import java.time.LocalDateTime;

@Builder
@Getter
public class PointHistoryResponse {
    private Long id;

    private Long userId;

    private int amount;

    private PointHistoryType type;

    private String description;

    private LocalDateTime createdAt;

    public static PointHistoryResponse from(PointHistory pointHistory) {
        return PointHistoryResponse.builder()
                .id(pointHistory.getId())
                .userId(pointHistory.getUserId())
                .amount(pointHistory.getAmount().getAmount())
                .type(pointHistory.getType())
                .description(pointHistory.getDescription())
                .createdAt(pointHistory.getCreatedAt())
                .build();
    }
}
