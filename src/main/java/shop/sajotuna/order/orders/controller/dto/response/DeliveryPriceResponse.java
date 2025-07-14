package shop.sajotuna.order.orders.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.sajotuna.order.orders.domain.DeliveryPrice;

@Getter
@AllArgsConstructor
public class DeliveryPriceResponse {
    private Integer freeDeliveryMinPrice;

    private Integer deliveryPrice;

    public static DeliveryPriceResponse of(DeliveryPrice deliveryPrice) {
        return new DeliveryPriceResponse(
                deliveryPrice.getFreeDeliveryMinPrice().getAmount(),
                deliveryPrice.getDeliveryPrice().getAmount()
        );
    }
}


