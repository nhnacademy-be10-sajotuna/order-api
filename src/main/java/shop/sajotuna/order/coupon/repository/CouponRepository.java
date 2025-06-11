package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.domain.UserCouponType;

import java.util.List;


public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
