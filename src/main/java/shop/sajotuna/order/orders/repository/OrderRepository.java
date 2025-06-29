package shop.sajotuna.order.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllBy(Pageable pageable);

    List<Order> findByOrdererUserId(long userId);

    Page<Order> findOrdersByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED' AND o.shippingInfo.shippingDate <= :time")
    List<Order> findShippedOrders(LocalDateTime time);
}
