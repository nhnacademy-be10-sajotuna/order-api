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
    private CouponType type;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    private Integer minOrderAmount;

    @Column(nullable = false)
    private Integer maxDiscountAmount;

    @Column(nullable = false)
    private Integer validDays;
}
