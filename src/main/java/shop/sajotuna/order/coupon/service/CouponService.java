package shop.sajotuna.order.coupon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.repository.CouponRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {
    private final CouponRepository couponRepository;



}
