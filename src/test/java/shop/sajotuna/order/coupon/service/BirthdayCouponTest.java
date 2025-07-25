package shop.sajotuna.order.coupon.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.client.BirthdayClient;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponPolicyType;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.dto.request.UserCouponRequest;
import shop.sajotuna.order.coupon.dto.response.BirthdayUserResponse;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.scheduler.BirthdayCouponScheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class BirthdayCouponTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    BirthdayClient birthdayClient;

    @InjectMocks
    private BirthdayCouponScheduler birthdayCouponScheduler;

    @Mock
    private UserCouponService userCouponService;

    @Test
    @DisplayName("생일 쿠폰 5명에게 발급 - 정상 작동")
    void issueBirthdayCoupons_FiveUsers() {
        // given
        List<BirthdayUserResponse> birthdayUsers = List.of(
                makeBirthdayUser(1L),
                makeBirthdayUser(2L),
                makeBirthdayUser(3L),
                makeBirthdayUser(4L),
                makeBirthdayUser(5L)
        );

        Coupon birthdayCoupon = Coupon.builder()
                .id(99L)
                .name("BIRTHDAY")
                .couponType(CouponType.ORDER)
                .policyType(CouponPolicyType.FIXED)
                .discountAmount(1000)
                .minOrderAmount(Money.of(10000))
                .maxDiscountAmount(Money.of(1000))
                .validDays(7)
                .build();

        when(birthdayClient.getBirthdayUsers()).thenReturn(birthdayUsers);
        when(couponRepository.findByName("BIRTHDAY")).thenReturn(Optional.of(birthdayCoupon));

        // when
        birthdayCouponScheduler.issueBirthdayCoupons();

        // then
        verify(birthdayClient, times(1)).getBirthdayUsers();
        verify(couponRepository, times(1)).findByName("BIRTHDAY");

        // 각각의 사용자에 대해 쿠폰 발급 호출 확인
        for (BirthdayUserResponse user : birthdayUsers) {
            verify(userCouponService, times(1)).saveUserCoupon(argThat(req ->
                    req.getUserId().equals(user.getId()) &&
                            req.getCouponId().equals(birthdayCoupon.getId())
            ));
        }

        // 정확히 5번 호출되었는지
        verify(userCouponService, times(5)).saveUserCoupon(any(UserCouponRequest.class));
    }

    private BirthdayUserResponse makeBirthdayUser(Long id) {
        BirthdayUserResponse user = new BirthdayUserResponse();
        user.setId(id);
        user.setBirthDate(LocalDateTime.now().toLocalDate());  // 오늘 생일로 가정
        return user;
    }

}
