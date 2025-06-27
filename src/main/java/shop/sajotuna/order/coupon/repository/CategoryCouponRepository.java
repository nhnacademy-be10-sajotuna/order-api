package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.sajotuna.order.coupon.domain.CouponSpecificCategory;

import java.util.List;

public interface CategoryCouponRepository extends JpaRepository<CouponSpecificCategory, Long> {
    boolean existsByCoupon_Id(Long couponId);

    void deleteByCoupon_Id(Long couponId);

    boolean existsByCouponIdAndCategoryIdIn(Long couponId, List<Long> categoryIds);
}
