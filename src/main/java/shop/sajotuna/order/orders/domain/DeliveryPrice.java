package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.common.exception.NullValueException;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class DeliveryPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "amount", column = @Column(name = "free_delivery_min_price", nullable = false))
    private Money freeDeliveryMinPrice;

    @AttributeOverride(name = "amount", column = @Column(name = "delivery_price", nullable = false))
    private Money deliveryPrice;

    public Money calculateDeliveryPrice(Money orderPrice) {
        if (orderPrice == null) {
            throw new NullValueException("주문 가격은 null일 수 없습니다.");
        }
        if (orderPrice.isGreaterThanOrEqual(freeDeliveryMinPrice)) {
            return Money.zero();
        }
        return deliveryPrice;
    }
}
