package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.coupon.domain.CouponSpecificBook;

public interface BookCouponRepository extends JpaRepository<CouponSpecificBook, Long> {
    boolean existsByCoupon_Id(Long couponId);

    void deleteByCoupon_Id(Long couponId);
}
