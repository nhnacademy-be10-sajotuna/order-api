package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.domain.OrderPackaging;

public interface OrderPackagingRepository extends JpaRepository<OrderPackaging, Long> {
    OrderPackaging getOrderPackagingById(Long id);
}
