package shop.sajotuna.order.point.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;

import java.math.RoundingMode;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class GradePointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @AttributeOverride(name = "amount", column = @Column(name = "min_total_order_price"))
    private Money minTotalOrderPrice;

    @AttributeOverride(name = "amount", column = @Column(name = "max_total_order_price"))
    private Money maxTotalOrderPrice;

    // 포인트 적립 비율 (예: 3%는 3으로 저장)
    private int pointRate;

    public Money calculatePoint(Money totalPrice) {
        return totalPrice.percentage(pointRate, RoundingMode.DOWN);
    }
}
