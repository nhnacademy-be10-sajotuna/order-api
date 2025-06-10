package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.coupon.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
