package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;

@Getter
@Embeddable
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Discounts {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "coupon_discount_amount"))
    })
    private Money couponDiscountAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "used_point"))
    })
    private Money usedPoint;

    public Discounts(Money couponDiscountAmount, Money usedPoint) {
        this.couponDiscountAmount = couponDiscountAmount;
        this.usedPoint = usedPoint;
    }

    public Money getTotalDiscountAmount() {
        return couponDiscountAmount.plus(usedPoint);
    }
}
