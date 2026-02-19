package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.sajotuna.order.coupon.domain.CouponSpecificCategory;

import java.util.List;
import java.util.Set;

public interface CategoryCouponRepository extends JpaRepository<CouponSpecificCategory, Long> {
    boolean existsByCoupon_Id(Long couponId);

    void deleteByCoupon_Id(Long couponId);

    boolean existsByCouponIdAndCategoryIdIn(Long couponId, Set<Long> categoryIds);

    @Query("SELECT DISTINCT csc.coupon.id FROM CouponSpecificCategory csc WHERE csc.coupon.id IN :couponIds AND csc.categoryId IN :categoryIds")
    Set<Long> findCouponIdsByCouponIdsAndCategoryIds(@Param("couponIds") Set<Long> couponIds, @Param("categoryIds") Set<Long> categoryIds);

    List<CouponSpecificCategory> findByCategoryIdIn(Set<Long> categoryIds);
}
