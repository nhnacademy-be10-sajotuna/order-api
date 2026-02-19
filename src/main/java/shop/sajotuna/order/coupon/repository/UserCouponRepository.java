package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.sajotuna.order.coupon.domain.UserCoupon;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @EntityGraph(value = "coupon")
    List<UserCoupon> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"coupon"})
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.id = :id")
    Optional<UserCoupon> findByIdWithCoupon(@Param("id") Long id);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    @Query("SELECT uc.coupon.id FROM UserCoupon uc WHERE uc.userId = :userId AND uc.type = 'AVAILABLE'")
    Set<Long> findAvailableCouponIdsByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"coupon"})
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.coupon.id = :couponId AND uc.type = 'AVAILABLE' ORDER BY uc.id ASC LIMIT 1")
    Optional<UserCoupon> findByUserIdAndCouponIdWithCoupon(Long userId, Long couponId);
}
