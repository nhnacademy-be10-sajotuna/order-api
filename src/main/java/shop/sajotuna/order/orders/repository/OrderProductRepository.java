package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.domain.OrderProduct;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> getOrderProductsByOrder_Id(Long orderId);

    void deleteByOrder_Id(Long orderId);
}
