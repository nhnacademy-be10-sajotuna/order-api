package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.sajotuna.order.orders.entity.Order;
import shop.sajotuna.order.orders.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(long userId);

    List<Order> findOrdersByStatus(OrderStatus orderStatus);

    @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED' AND o.shippingDate <= :time")
    List<Order> findShippedOrders(LocalDateTime time);
}
