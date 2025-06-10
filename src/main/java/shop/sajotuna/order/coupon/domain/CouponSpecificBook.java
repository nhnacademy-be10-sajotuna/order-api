package shop.sajotuna.order.coupon.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.ISBN;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponSpecificBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookCouponId;

    @Column(nullable = false)
    private String isbn;


    @ManyToOne
    private Coupon coupon;

}
