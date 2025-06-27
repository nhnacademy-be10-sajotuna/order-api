package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    @Embedded
    private Money amount;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private PointHistoryType type;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime createdAt;

    private PointHistory(Long userId, Money amount, PointHistoryType type, String description) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public static PointHistory createRedeemHistory(Long userId, Money amount, String description) {
        return new PointHistory(userId, amount, PointHistoryType.REDEEMED, description);
    }

    public static PointHistory createEarnHistory(Long userId, Money amount, String description) {
        return new PointHistory(userId, amount, PointHistoryType.EARNED, description);
    }
}
