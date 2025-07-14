package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
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

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "earned_point"))
    private Money earnedPoint;

    private Long usedCouponId;

    public Discounts(Money couponDiscountAmount, Money usedPoint, Long usedCouponId) {
        validateDiscounts(couponDiscountAmount, usedPoint);
        this.couponDiscountAmount = couponDiscountAmount;
        this.usedPoint = usedPoint;
        this.usedCouponId = usedCouponId;
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

    public void setEarnedPoint(Money earnedPoint) {
        if (earnedPoint == null) {
            throw new NullValueException("적립된 포인트는 필수입니다.");
        }
        this.earnedPoint = earnedPoint;
    }
}
