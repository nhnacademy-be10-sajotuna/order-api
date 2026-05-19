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

    @Query("""
            SELECT COALESCE(SUM(
                o.orderPrice.totalProductPrice.amount
                - o.discounts.couponDiscountAmount.amount
                - o.discounts.usedPoint.amount
            ), 0)
            FROM Order o
            WHERE o.orderer.userId = :userId
              AND o.createdAt >= :createdAt
              AND o.status IN :statuses
            """)
    Long sumRecentOrderAmount(
            @Param("userId") Long userId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("statuses") List<OrderStatus> statuses
    );

    @Query("""
            SELECT DISTINCT o.orderer.userId
            FROM Order o
            WHERE o.orderer.userId IS NOT NULL
              AND o.createdAt >= :from
              AND o.createdAt < :to
              AND o.status IN :statuses
            """)
    List<Long> findUserIdsWithOrdersExpiringFromGradeWindow(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("statuses") List<OrderStatus> statuses
    );

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderProducts WHERE o.id = :orderId")
    Optional<Order> findByIdWithOrderProducts(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderProducts WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithOrderProducts(@Param("orderNumber") String orderNumber);
}
