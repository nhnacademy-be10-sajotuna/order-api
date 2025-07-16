package shop.sajotuna.order.coupon.rabbitmq;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponFatalMessageRepository extends JpaRepository<CouponFatalMessageLog, Long> {
}
