package shop.sajotuna.order.orders.domain;

import jakarta.persistence.*;
import lombok.*;
import shop.sajotuna.order.common.domain.Money;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@ToString
@EqualsAndHashCode
public class OrderPrice {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_product_price"))
    })
    private Money totalProductPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "packaging_price"))
    })
    private Money packagingPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "delivery_price"))
    })
    private Money deliveryPrice;

    public Money getTotalPrice() {
        return totalProductPrice.plus(packagingPrice).plus(deliveryPrice);
    }

    private OrderPrice(Money totalProductPrice, Money packagingPrice, Money deliveryPrice) {
        validateOrderPrice(totalProductPrice, packagingPrice, deliveryPrice);
        this.totalProductPrice = totalProductPrice;
        this.packagingPrice = packagingPrice;
        this.deliveryPrice = deliveryPrice;
    }

    private void validateOrderPrice(Money totalProductPrice, Money packagingPrice, Money deliveryPrice) {
        if (totalProductPrice == null) {
            throw new IllegalArgumentException("상품 총액은 필수입니다.");
        }
        if (packagingPrice == null) {
            throw new IllegalArgumentException("포장비는 필수입니다.");
        }
        if (deliveryPrice == null) {
            throw new IllegalArgumentException("배송비는 필수입니다.");
        }
        if (!totalProductPrice.isPositive()) {
            throw new IllegalArgumentException("상품 총액은 0원보다 커야 합니다.");
        }
    }
}
