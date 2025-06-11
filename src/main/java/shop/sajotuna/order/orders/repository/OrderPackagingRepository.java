package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import shop.sajotuna.order.orders.entity.OrderPackaging;

public interface OrderPackagingRepository extends JpaRepository<OrderPackaging, Long> {
    OrderPackaging getOrderPackagingById(Long id);

    @Modifying
    @Query("update OrderPackaging o set o.packaging = :packaging, o.price = :price where o.id = :id")
    void updateOrderPackaging(String packaging, Integer price, Long id);
}
