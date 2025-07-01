package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.orders.domain.DeliveryPrice;

public interface DeliveryPriceRepository extends JpaRepository<DeliveryPrice, Long> {

    long DEFAULT_DELIVERY_PRICE_ID = 1L;

    default DeliveryPrice getDefaultDeliveryPrice() {
        return findById(DEFAULT_DELIVERY_PRICE_ID).get();
    }
}