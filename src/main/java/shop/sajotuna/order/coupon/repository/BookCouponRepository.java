package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponSpecificBook;

import java.util.List;
import java.util.Optional;

public interface BookCouponRepository extends JpaRepository<CouponSpecificBook, Long> {
    boolean existsByCoupon_Id(Long couponId);

    void deleteByCoupon_Id(Long couponId);

    boolean existsByCouponIdAndIsbn(Long couponId, String isbn);

    List<CouponSpecificBook> findByIsbn(String isbn);

}
