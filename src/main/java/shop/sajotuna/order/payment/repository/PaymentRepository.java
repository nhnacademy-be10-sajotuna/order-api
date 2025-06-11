package shop.sajotuna.order.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
