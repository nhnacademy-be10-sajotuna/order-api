package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import shop.sajotuna.common.exception.NegativePointException;

@Entity
@Getter
public class UserPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    @PositiveOrZero
    private Long remainPoint;

    @Version
    private Long version;

    public void earnPoint(long amount) {
        if (amount < 0) {
            throw new NegativePointException();
        }
        this.remainPoint += amount;
    }

    public void redeemPoint(int pointAmount) {
        if (remainPoint < pointAmount) {
            throw new InsufficientPointException(remainPoint, pointAmount - remainPoint);
        }
        if (pointAmount < 0) {
            throw new NegativePointException();
        }
        this.remainPoint -= pointAmount;
    }
}
