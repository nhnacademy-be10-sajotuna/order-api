package shop.sajotuna.order.coupon.domain;

import jakarta.persistence.*;
import lombok.*;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;
import shop.sajotuna.order.coupon.dto.request.CouponRequest;

import java.math.RoundingMode;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType couponType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponPolicyType policyType;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    @AttributeOverride(name = "amount", column = @Column(name = "min_order_amount"))
    private Money minOrderAmount;

    @Column(nullable = false)
    @AttributeOverride(name = "amount", column = @Column(name = "max_discount_amount"))
    private Money maxDiscountAmount;

    @Column(nullable = false)
    private Integer validDays;

    public void updateCoupon(CouponRequest couponRequest) {
        this.name = couponRequest.getName();
        this.couponType = couponRequest.getCouponType();
        this.policyType = couponRequest.getPolicyType();
        this.discountAmount = couponRequest.getDiscountAmount();
        this.minOrderAmount = Money.of(couponRequest.getMinOrderAmount());
        this.maxDiscountAmount = Money.of(couponRequest.getMaxDiscountAmount());
        this.validDays = couponRequest.getValidDays();
    }

    public Money calculateDiscount(Money totalProductPrice) {
        if (totalProductPrice == null) {
            throw new NullValueException("주문 금액은 null일 수 없습니다.");
        }

        if (totalProductPrice.isLessThan(this.minOrderAmount)) {
            return Money.zero();
        }

        if (policyType == CouponPolicyType.FIXED) {
            return Money.of(discountAmount);
        }

        Money discount = totalProductPrice.percentage(discountAmount, RoundingMode.DOWN);
        if (discount.isGreaterThan(this.maxDiscountAmount)) {
            discount = this.maxDiscountAmount;
        }
        // 할인 금액이 주문 금액을 초과하지 않도록 제한
        if (discount.isGreaterThan(totalProductPrice)) {
            discount = totalProductPrice;
        }
        return discount;
    }
}
