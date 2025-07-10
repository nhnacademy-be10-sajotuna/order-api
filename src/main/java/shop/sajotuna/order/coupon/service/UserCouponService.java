package shop.sajotuna.order.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.domain.UserCouponType;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.dto.request.UserCouponRequest;
import shop.sajotuna.order.coupon.dto.response.UserCouponDetailResponse;
import shop.sajotuna.order.coupon.dto.response.UserCouponResponse;
import shop.sajotuna.order.coupon.repository.BookCouponRepository;
import shop.sajotuna.order.coupon.repository.CategoryCouponRepository;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCouponService {
    public static final String COUPON_NAME = "WELCOME";
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;

    // 유저가 가진 쿠폰 목록 조회
    @Transactional
    public List<UserCouponDetailResponse> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        userCoupons.forEach(UserCoupon::updateExpiredCoupon);

        return userCoupons.stream()
                .map(userCoupon ->
                        UserCouponDetailResponse.from(userCoupon, userCoupon.getCoupon()))
                .toList();
    }

    @Transactional
    public List<UserCouponDetailResponse> getAllAvailableCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        userCoupons.forEach(UserCoupon::updateExpiredCoupon);

        List<UserCoupon> availableCoupons = userCoupons.stream()
                .filter(coupon -> coupon.getType() == UserCouponType.AVAILABLE)
                .toList();

        return availableCoupons.stream()
                .map(userCoupon -> UserCouponDetailResponse.from(userCoupon, userCoupon.getCoupon()))
                .toList();
    }

    // 유저 쿠폰 생성
    @Transactional
    public UserCouponResponse saveUserCoupon(UserCouponRequest userCouponRequest) {
        Coupon coupon = couponRepository.findById(userCouponRequest.getCouponId()).orElseThrow(CouponNotFoundException::new);

        UserCoupon userCoupon = userCouponRepository.save(new UserCoupon(coupon, userCouponRequest.getUserId(), userCouponRequest.getIssuedAt(), coupon.getValidDays()));

        return UserCouponResponse.from(userCoupon);
    }

    // 웰컴 쿠폰 발급
    @Transactional
    public UserCouponResponse issueWelcomeCoupon(Long userId) {
        Coupon coupon = couponRepository.findByName(COUPON_NAME).orElseThrow(CouponNotFoundException::new);

        UserCoupon userCoupon = new UserCoupon(coupon, userId, LocalDateTime.now(), coupon.getValidDays());
        userCouponRepository.save(userCoupon);

        return UserCouponResponse.from(userCoupon);
    }

    // 사용 가능한 책 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponResponse> getAvailableCoupons(Long userId, BookInfo bookInfo) {
        log.info("bookInfo categoryIds: {}", bookInfo.getCategoryIds().toString());
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        userCoupons.forEach(UserCoupon::updateExpiredCoupon);

        List<UserCoupon> availableCoupons = userCoupons.stream().filter(coupon -> coupon.getType() == UserCouponType.AVAILABLE).toList();

        List<Coupon> coupons = availableCoupons.stream().map(UserCoupon::getCoupon).toList();
        List<CouponResponse> result = new ArrayList<>();
        for (Coupon coupon : coupons) {

            if (bookCouponRepository.existsByCouponIdAndIsbn(coupon.getId(), bookInfo.getIsbn())) {
                result.add(CouponResponse.from(coupon));
            }
            if (categoryCouponRepository.existsByCouponIdAndCategoryIdIn(coupon.getId(), bookInfo.getCategoryIds())) {
                result.add(CouponResponse.from(coupon));
            }
        }
        return result;
    }

    // 사용 가능한 오더 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponResponse> getAvailableOrderCoupons(Long userId, Money totalPrice){
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

        userCoupons.forEach(UserCoupon::updateExpiredCoupon);

        List<UserCoupon> availableCoupons = userCoupons.stream()
                .filter(coupon -> coupon.getType() == UserCouponType.AVAILABLE)
                .toList();

        List<Coupon> coupons = availableCoupons.stream().map(UserCoupon::getCoupon)
                .filter(coupon -> coupon.getCouponType() == CouponType.ORDER)
                .filter(coupon -> totalPrice.isGreaterThan(coupon.getMinOrderAmount()))
                .toList();
        return coupons.stream().map(CouponResponse::from).toList();
    }

}
