package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.entity.GuestOrder;

public interface GuestOrderRepository extends JpaRepository<GuestOrder, Long> {
}
