package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import shop.sajotuna.order.point.exception.InvalidPriceException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PointPolicyType type;

    private Integer fixedPoint;

    @Column(precision=4, scale=3)
    private BigDecimal rate;

    /**
     * 구매 적립 시 포인트 계산을 위해 사용하는 메서드
     */
    public int calculatePoint(int totalPrice) {
        if (totalPrice < 0) {
            throw new InvalidPriceException(totalPrice);
        }
        BigDecimal price = BigDecimal.valueOf(totalPrice);
        return price.multiply(rate).setScale(0, RoundingMode.DOWN).intValue();
    }
}
