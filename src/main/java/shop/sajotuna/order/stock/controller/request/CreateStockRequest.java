package shop.sajotuna.order.stock.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateStockRequest {

    @NotBlank
    private String isbn;

    @Min(0)
    private int stock;
}
