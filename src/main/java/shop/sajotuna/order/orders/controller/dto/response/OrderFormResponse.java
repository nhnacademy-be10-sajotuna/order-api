package shop.sajotuna.order.orders.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.sajotuna.order.coupon.dto.response.UserCouponDetailResponse;

import java.util.List;

@Getter
@Builder
public class OrderFormResponse {

    //패키지 정보
    private List<PackageResponse> packages;

    //포인트
    private Integer point;

    //쿠폰
    private List<UserCouponDetailResponse> coupons;

    //배달비
    private DeliveryPriceResponse deliveryPrice;
}
