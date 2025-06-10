package shop.sajotuna.order.coupon.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon_history")
public class CouponHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer couponId;

    @Column(nullable = false)
    private LocalDate issuedAt;

    @Column(nullable = false)
    private CouponHistoryType couponHistoryType;

    @Column(nullable = false)
    private Integer userId;


}
