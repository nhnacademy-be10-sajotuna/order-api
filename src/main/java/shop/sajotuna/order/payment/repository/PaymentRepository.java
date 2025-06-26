package shop.sajotuna.order.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment getPaymentByOrder_Id(Long orderId);
}
