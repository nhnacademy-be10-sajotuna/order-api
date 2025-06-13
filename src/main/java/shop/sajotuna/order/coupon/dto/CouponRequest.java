package shop.sajotuna.order.coupon.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponType;

@Data
@AllArgsConstructor
public class CouponRequest {
    @NotBlank
    private String name;

    @NotNull
    private CouponType type;

    @NotNull
    @Min(0)
    private Integer discountAmount;

    @NotNull
    @Min(0)
    private Integer minOrderAmount;

    @NotNull
    @Min(0)
    private Integer maxDiscountAmount;

    @NotNull
    @Min(0)
    private Integer validDays;

    public Coupon toEntity() {
        return new Coupon(name, type, discountAmount, minOrderAmount, maxDiscountAmount, validDays);
    }
}
