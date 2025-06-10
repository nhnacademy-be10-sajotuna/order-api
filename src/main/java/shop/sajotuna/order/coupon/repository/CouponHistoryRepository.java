package shop.sajotuna.order.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.coupon.domain.CouponHistory;

public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Long> {
}
