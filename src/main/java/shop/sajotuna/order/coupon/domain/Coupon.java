package shop.sajotuna.order.coupon.domain;

import jakarta.persistence.*;
import lombok.*;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;

@Getter
@Setter
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "coupon")
//TODO: Coupon 내부 금액 관련 필드 Money로 변경
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
    private Integer minOrderAmount;

    @Column(nullable = false)
    private Integer maxDiscountAmount;

    @Column(nullable = false)
    private Integer validDays;

    public int calculateDiscount(int orderAmount) {
        if (orderAmount < minOrderAmount) {
            return 0;
        }

        int discount;
        if (policyType == CouponPolicyType.FIXED) {
            discount = discountAmount;
        } else {
            discount = (int) Math.floor(orderAmount * (discountAmount / 100.0));
            discount = Math.min(discount, maxDiscountAmount);
        }

        return Math.min(discount, orderAmount);
    }

    //TODO: Money로 변경 후 수정
    public Money calculateDiscount(Money totalProductPrice) {
        if (totalProductPrice == null) {
            throw new NullValueException("주문 금액은 null일 수 없습니다.");
        }

        Money minOrderMoney = Money.of(minOrderAmount);
        if (totalProductPrice.isLessThan(minOrderMoney)) {
            return Money.zero();
        }

        Money discount;
        if (policyType == CouponPolicyType.FIXED) {
            discount = Money.of(discountAmount);
        } else {
            // 백분율 할인 계산: (주문금액 * 할인율) / 100
            discount = totalProductPrice.multiply(discountAmount).divide(100);

            Money maxDiscountMoney = Money.of(maxDiscountAmount);
            if (discount.isGreaterThan(maxDiscountMoney)) {
                discount = maxDiscountMoney;
            }
        }

        // 할인 금액이 주문 금액을 초과하지 않도록 제한
        if (discount.isGreaterThan(totalProductPrice)) {
            discount = totalProductPrice;
        }

        return discount;
    }
}
