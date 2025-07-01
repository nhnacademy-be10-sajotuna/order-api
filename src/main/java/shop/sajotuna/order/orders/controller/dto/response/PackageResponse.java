package shop.sajotuna.order.orders.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.orders.domain.OrderPackaging;

@Getter
@Builder
public class PackageResponse {
    private Long id;
    private String packaging;
    private Integer price;

    public static PackageResponse from(OrderPackaging orderPackaging) {
        return PackageResponse.builder()
                .id(orderPackaging.getId())
                .packaging(orderPackaging.getPackaging())
                .price(orderPackaging.getPrice().getAmount())
                .build();
    }
}
