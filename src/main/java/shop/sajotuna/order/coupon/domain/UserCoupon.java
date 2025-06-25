package shop.sajotuna.order.coupon.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.sajotuna.order.coupon.exception.ExpiredCouponException;
import shop.sajotuna.order.coupon.exception.AlreadyUsedCouponException;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_coupon")
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserCouponType type;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    public UserCoupon(Coupon coupon, Long userId, LocalDateTime issuedAt, Integer validDays) {
        this.coupon = coupon;
        this.userId = userId;
        this.type = UserCouponType.AVAILABLE;
        this.issuedAt = issuedAt;
        this.expiresAt = issuedAt.plusDays(validDays);
    }

    public int applyCoupon(int totalProductPrice) {
        if (type == UserCouponType.USED || type == UserCouponType.EXPIRED) {
            throw new AlreadyUsedCouponException();
        }
        if (LocalDateTime.now().isAfter(expiresAt)) {
            type = UserCouponType.EXPIRED;
            throw new ExpiredCouponException();
        }

        this.type = UserCouponType.USED;
        return coupon.calculateDiscount(totalProductPrice);
    }
}
