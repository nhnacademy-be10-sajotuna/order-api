package shop.sajotuna.order.coupon.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponPolicyType;
import shop.sajotuna.order.coupon.domain.CouponType;

@Data
@AllArgsConstructor
public class CouponRequest {
    @NotBlank
    private String name;

    @NotNull
    private CouponType couponType;

    @NotNull
    private CouponPolicyType policyType;

    @NotNull
    @Min(1)
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
        return Coupon.builder()
                .name(name)
                .couponType(couponType)
                .policyType(policyType)
                .discountAmount(discountAmount)
                .minOrderAmount(Money.of(minOrderAmount))
                .maxDiscountAmount(Money.of(maxDiscountAmount))
                .validDays(validDays).build();
    }
}
