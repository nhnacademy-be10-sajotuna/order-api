package shop.sajotuna.order.coupon.domain;

import jakarta.persistence.*;
import lombok.*;

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
}
