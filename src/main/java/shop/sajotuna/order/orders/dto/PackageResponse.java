package shop.sajotuna.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PackageResponse {
    private Long id;
    private String packaging;
    private Integer price;
}
