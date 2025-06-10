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
public class CouponSpecificCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryCouponId;

    @Column(nullable = false)
    private int categoryId;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;



}
