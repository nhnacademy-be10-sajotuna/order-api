package shop.sajotuna.order.coupon.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponPolicyId;

    @Column(name = "type", nullable = false)
    private CouponType couponType;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "min_order_amount", nullable = false)
    private Integer minOrderAmount;

    @Column(name = "max_order_amount", nullable = false)
    private Integer maxOrderAmount;

}
