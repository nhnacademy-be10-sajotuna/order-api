package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;

@Getter
@Embeddable
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Discounts {

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "coupon_discount_amount"))
    private Money couponDiscountAmount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "used_point"))
    private Money usedPoint;

    public Discounts(Money couponDiscountAmount, Money usedPoint) {
        validateDiscounts(couponDiscountAmount, usedPoint);
        this.couponDiscountAmount = couponDiscountAmount;
        this.usedPoint = usedPoint;
    }

    private void validateDiscounts(Money couponDiscountAmount, Money usedPoint) {
        if (couponDiscountAmount == null) {
            throw new NullValueException("쿠폰 할인 금액은 필수입니다.");
        }
        if (usedPoint == null) {
            throw new NullValueException("사용한 포인트는 필수입니다.");
        }
    }

    public Money getTotalDiscountAmount() {
        return couponDiscountAmount.plus(usedPoint);
    }
}
