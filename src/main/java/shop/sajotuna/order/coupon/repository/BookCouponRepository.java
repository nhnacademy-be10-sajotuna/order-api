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

    List<CouponSpecificBook> findByIsbn(String isbn);

    @Query("""
            SELECT b.coupon.id
            FROM CouponSpecificBook b
            WHERE b.coupon.id IN :couponIds
              AND b.isbn = :isbn
            """)
    Set<Long> findCouponIdsByCouponIdInAndIsbn(
            @Param("couponIds") Set<Long> couponIds,
            @Param("isbn") String isbn
    );
}
