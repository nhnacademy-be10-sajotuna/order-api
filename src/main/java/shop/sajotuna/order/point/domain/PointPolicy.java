package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.exception.InvalidPercentageException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointPolicy {

    public static final int MAX_PERCENTAGE = 1000;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PointPolicyType type;

    @NotNull
    @Min(1)
    private int value;

    @Enumerated(EnumType.STRING)
    @NotNull
    private CalculationMode calculationMode;

    /**
     * 구매 적립 시 포인트 계산을 위해 사용하는 메서드
     * calculationMode가 FIXED인 경우에는 value를 그대로 반환하고,
     * calculationMode가 RATE인 경우에는 totalPrice에 대한 비율을 계산하여 포인트를 반환합니다.
     * ex) value = 15인 경우 1.5%의 비율로 포인트를 계산합니다.
     */
    public Money calculatePoint(Money totalPrice) {
        if (calculationMode == CalculationMode.FIXED) {
            return Money.of(value);
        }
        BigDecimal rate = BigDecimal.valueOf(value).movePointLeft(3);
        int pointAmount = BigDecimal.valueOf(totalPrice.getAmount()).multiply(rate).setScale(0, RoundingMode.DOWN).intValue();
        return Money.of(pointAmount);
    }

    public void update(int value) {
        if (calculationMode == CalculationMode.RATE) {
            if (value > MAX_PERCENTAGE) {
                throw new InvalidPercentageException();
            }
        }
        this.value = value;
    }
}
