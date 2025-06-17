package shop.sajotuna.order.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserCouponRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long couponId;
    @NotNull
    @JsonFormat(pattern = "yyyyMMddHHmmSS")
    private LocalDateTime issuedAt;
}
