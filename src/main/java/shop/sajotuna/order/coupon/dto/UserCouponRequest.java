package shop.sajotuna.order.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCouponRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long couponId;
    @NotNull
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime issuedAt;
}
