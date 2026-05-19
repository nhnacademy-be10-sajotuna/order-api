package shop.sajotuna.order.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.coupon.dto.response.UserCouponDetailResponse;
import shop.sajotuna.order.coupon.service.UserCouponService;
import shop.sajotuna.order.orders.controller.dto.response.DeliveryPriceResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderFormResponse;
import shop.sajotuna.order.orders.controller.dto.response.PackageResponse;
import shop.sajotuna.order.orders.domain.DeliveryPrice;
import shop.sajotuna.order.orders.repository.DeliveryPriceRepository;
import shop.sajotuna.order.orders.service.product.PackageService;
import shop.sajotuna.order.point.service.PointService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderFormService {
    
    private final PointService pointService;
    private final UserCouponService userCouponService;
    private final PackageService packageService;
    private final DeliveryPriceRepository deliveryPriceRepository;

    public OrderFormResponse getOrderForm(Long userId) {

        List<PackageResponse> packages = packageService.getPackages();
        DeliveryPrice deliveryPrice = deliveryPriceRepository.getDefaultDeliveryPrice();

        if (userId == null) {
            return OrderFormResponse.builder()
                    .packages(packages)
                    .deliveryPrice(DeliveryPriceResponse.of(deliveryPrice))
                    .build();
        }

        Integer point = pointService.getAvailablePointByUserId(userId);
        List<UserCouponDetailResponse> coupons = userCouponService.getAllAvailableCoupons(userId);

        return OrderFormResponse.builder()
                .point(point)
                .coupons(coupons)
                .packages(packages)
                .deliveryPrice(DeliveryPriceResponse.of(deliveryPrice))
                .build();
    }
}
