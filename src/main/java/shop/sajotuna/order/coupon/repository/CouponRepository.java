package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.coupon.domain.Coupon;

import java.util.Optional;


public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByName(String name);
}
