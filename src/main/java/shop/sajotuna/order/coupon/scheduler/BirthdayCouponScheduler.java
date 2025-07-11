package shop.sajotuna.order.coupon.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.coupon.client.BirthdayClient;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.dto.response.BirthdayUserResponse;
import shop.sajotuna.order.coupon.dto.request.UserCouponRequest;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.service.UserCouponService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BirthdayCouponScheduler {
    public static final String COUPON_NAME = "BIRTHDAY";
    private final BirthdayClient birthdayClient;
    private final UserCouponService userCouponService;
    private final CouponRepository couponRepository;

    @Scheduled(cron = "0 0 0 1 * ?") // 매달 1일 0시
    public void issueBirthdayCoupons() {
        List<BirthdayUserResponse> users = birthdayClient.getBirthdayUsers();
        for (BirthdayUserResponse user : users) {
            Coupon birthdayCoupon = couponRepository.findByName(COUPON_NAME).orElseThrow(()-> new CouponNotFoundException(COUPON_NAME));
            UserCouponRequest userCouponRequest = new UserCouponRequest(user.getId(), birthdayCoupon.getId(), LocalDateTime.now());

            userCouponService.saveUserCoupon(userCouponRequest);
        }
    }

}
