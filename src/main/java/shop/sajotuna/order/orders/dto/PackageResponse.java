package shop.sajotuna.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.sajotuna.order.orders.domain.OrderPackaging;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageResponse {
    private Long id;
    private String packaging;
    private Integer price;

    public static PackageResponse from(OrderPackaging orderPackaging) {
        return new PackageResponse(orderPackaging.getId(), orderPackaging.getPackaging(), orderPackaging.getPrice());
    }
}
