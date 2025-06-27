package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.domain.GuestOrder;

public interface GuestOrderRepository extends JpaRepository<GuestOrder, Long> {
}
