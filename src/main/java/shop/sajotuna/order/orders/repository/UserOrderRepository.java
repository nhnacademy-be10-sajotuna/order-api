package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.entity.UserOrder;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
}
