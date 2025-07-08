package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.domain.OrderStatus;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> getOrderProductsByOrder_Id(Long orderId);

    void deleteByOrder_Id(Long orderId);

    boolean existsByOrderOrdererUserIdAndIsbnAndOrderStatus(Long userId, String isbn, OrderStatus status);
}
