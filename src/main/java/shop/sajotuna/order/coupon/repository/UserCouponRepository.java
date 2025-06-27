package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.coupon.domain.UserCoupon;

import java.util.List;


public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @EntityGraph(value = "coupon")
    List<UserCoupon> findByUserId(Long userId);
}

