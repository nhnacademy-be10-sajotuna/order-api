package shop.sajotuna.order.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.payment.entity.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment getPaymentByOrder_Id(Long orderId);

    boolean existsByOrder_Id(Long orderId);

    List<Payment> getPaymentsByOrder_UserId(Long orderUserId);
}
