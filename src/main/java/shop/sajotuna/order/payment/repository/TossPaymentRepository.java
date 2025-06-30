package shop.sajotuna.order.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.payment.domain.TossPayment;

public interface TossPaymentRepository extends JpaRepository<TossPayment, Long> {
}
