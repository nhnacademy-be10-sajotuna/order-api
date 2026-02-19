package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.sajotuna.order.coupon.domain.CouponSpecificBook;

import java.util.List;
import java.util.Set;

public interface BookCouponRepository extends JpaRepository<CouponSpecificBook, Long> {
    boolean existsByCoupon_Id(Long couponId);

    void deleteByCoupon_Id(Long couponId);

    boolean existsByCouponIdAndIsbn(Long couponId, String isbn);

    @Query("SELECT DISTINCT csb.coupon.id FROM CouponSpecificBook csb WHERE csb.coupon.id IN :couponIds AND csb.isbn = :isbn")
    Set<Long> findCouponIdsByCouponIdsAndIsbn(@Param("couponIds") Set<Long> couponIds, @Param("isbn") String isbn);

    List<CouponSpecificBook> findByIsbn(String isbn);

}
