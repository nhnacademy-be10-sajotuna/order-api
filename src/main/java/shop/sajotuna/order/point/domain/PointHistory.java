package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @PositiveOrZero
    private int amount;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private PointType type;

    @NotNull
    private LocalDateTime createdAt;

    private PointHistory(Long userId, int amount, PointType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public static PointHistory createRedeemHistory(Long userId, int amount) {
        return new PointHistory(userId, amount, PointType.REDEEMED);
    }

    public static PointHistory createEarnHistory(Long userId, int amount) {
        return new PointHistory(userId, amount, PointType.EARNED);
    }
}
