package shop.sajotuna.order.coupon.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
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
    private CouponType type;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    private Integer minOrderAmount;

    @Column(nullable = false)
    private Integer maxDiscountAmount;

    @Column(nullable = false)
    private Integer validDays;

    public Coupon(String name, CouponType type, Integer discountAmount, Integer minOrderAmount, Integer maxDiscountAmount, Integer validDays) {
        this.name = name;
        this.type = type;
        this.discountAmount = discountAmount;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.validDays = validDays;
    }
}
