package shop.sajotuna.order.orders.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class OrderPrice {
    private int totalProductPrice;
    private int packagingPrice;
    private int deliveryPrice;
}
