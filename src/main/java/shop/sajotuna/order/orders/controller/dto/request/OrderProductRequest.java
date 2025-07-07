package shop.sajotuna.order.orders.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.Set;

@Data
public class OrderProductRequest {
    private long orderPackagingId;

    @NotBlank
    private String isbn;

    @NotNull
    @PositiveOrZero
    private Integer qty;

    @NotNull
    @PositiveOrZero
    private Integer amount;

    // 상품별 쿠폰 ID (선택적)
    private Long bookCouponId;

    @NotNull
    private Boolean packagingRequest;

    // 카테고리 ID 목록 (선택적)
    private Set<Long> categoryIds;
}
