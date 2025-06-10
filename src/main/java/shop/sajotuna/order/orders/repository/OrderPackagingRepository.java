package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.entity.OrderPackaging;

public interface OrderPackagingRepository extends JpaRepository<OrderPackaging, Integer> {
}
