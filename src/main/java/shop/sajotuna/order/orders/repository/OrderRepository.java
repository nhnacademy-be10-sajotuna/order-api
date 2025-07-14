package shop.sajotuna.order.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllBy(Pageable pageable);

    Page<Order> findOrdersByOrdererUserIdOrderByCreatedAtDesc(Long ordererUserId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status order by o.shippingInfo.expectedDeliveryDate asc")
    Page<Order> findOrdersByStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED' AND o.shippingInfo.shippingStartDate <= :time")
    List<Order> findShippedOrders(LocalDateTime time);

    Order findOrderByOrderNumber(String orderNumber);

    List<Order> findByOrdererUserIdAndCreatedAtAfter(Long userId, LocalDateTime createdAt);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderProducts WHERE o.id = :orderId")
    Optional<Order> findByIdWithOrderProducts(@Param("orderId") Long orderId);
}
