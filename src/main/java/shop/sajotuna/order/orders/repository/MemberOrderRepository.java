package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.domain.MemberOrder;
import shop.sajotuna.order.orders.domain.Orders;

public interface MemberOrderRepository extends JpaRepository<MemberOrder, Long> {
}
