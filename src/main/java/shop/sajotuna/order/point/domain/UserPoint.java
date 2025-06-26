package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.exception.InvalidUserIdException;
import shop.sajotuna.order.point.exception.NegativePointException;
import shop.sajotuna.order.point.exception.InsufficientPointException;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private Long userId;

    @NotNull
    @PositiveOrZero
    private Money remainPoint;

    @Version
    private Long version;

    public void earnPoint(Money amount) {
        if (!amount.isPositive()) {
            throw new NegativePointException();
        }
        this.remainPoint = this.remainPoint.plus(amount);
    }

    public void redeemPoint(Money pointAmount) {
        if (remainPoint.isLessThan(pointAmount)) {
            throw new InsufficientPointException(remainPoint, pointAmount.minus(remainPoint));
        }
        if (!pointAmount.isPositive()) {
            throw new NegativePointException();
        }
        this.remainPoint = this.remainPoint.minus(pointAmount);
    }

    public static UserPoint create(Long userId) {
        if (userId == null) {
            throw new InvalidUserIdException();
        }
        return new UserPoint(null, userId, Money.zero(), null);
    }
}
