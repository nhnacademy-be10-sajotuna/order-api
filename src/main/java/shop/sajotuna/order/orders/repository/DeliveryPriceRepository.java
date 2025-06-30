package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.domain.DeliveryPrice;

public interface DeliveryPriceRepository extends JpaRepository<DeliveryPrice, Long> {

    default DeliveryPrice getDefaultDeliveryPrice() {
        return findById(1L).get();
    }
}