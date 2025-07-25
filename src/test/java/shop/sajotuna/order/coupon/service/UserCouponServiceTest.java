package shop.sajotuna.order.coupon.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.*;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.request.UserCouponRequest;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.dto.response.UserCouponDetailResponse;
import shop.sajotuna.order.coupon.dto.response.UserCouponResponse;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.BookCouponRepository;
import shop.sajotuna.order.coupon.repository.CategoryCouponRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {

    @Mock
    private BookCouponRepository bookCouponRepository;

    @Mock
    private CategoryCouponRepository categoryCouponRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    private Coupon createCoupon(Long id, String name) {
        return Coupon.builder()
                .id(id)
                .name(name)
                .couponType(CouponType.ORDER)
                .policyType(CouponPolicyType.FIXED)
                .discountAmount(1000)
                .minOrderAmount(Money.of(10000))
                .maxDiscountAmount(Money.of(5000))
                .validDays(30)
                .build();
    }

    private UserCoupon createUserCoupon(Long id, UserCouponType type, Coupon coupon) {
        LocalDateTime issuedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime expiresAt = issuedAt.plusDays(30);
        Long userId = 1L;

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setId(id);
        userCoupon.setIssuedAt(issuedAt);
        userCoupon.setExpiresAt(expiresAt);
        userCoupon.setType(type);
        userCoupon.setUserId(userId);
        userCoupon.setCoupon(coupon);

        return userCoupon;
    }
    @Test
    @DisplayName("유저 쿠폰 생성 - 성공")
    void saveUserCoupon_success() {
        // given
        Long userId = 1L;
        Long couponId = 100L;
        LocalDateTime issuedAt = LocalDateTime.now();

        UserCouponRequest request = new UserCouponRequest(userId, couponId, issuedAt);

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .name("할인쿠폰")
                .couponType(CouponType.ORDER)
                .policyType(CouponPolicyType.FIXED)
                .discountAmount(3000)
                .minOrderAmount(Money.of(20000))
                .maxDiscountAmount(Money.of(5000))
                .validDays(7)
                .build();

        UserCoupon savedUserCoupon = new UserCoupon(coupon, userId, issuedAt, coupon.getValidDays());
        savedUserCoupon.setId(999L);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(savedUserCoupon);

        // when
        UserCouponResponse response = userCouponService.saveUserCoupon(request);

        // then
        assertNotNull(response);
        assertEquals(savedUserCoupon.getId(), response.getUserCouponId());
        assertEquals(issuedAt, response.getIssuedAt());
        assertEquals(savedUserCoupon.getExpiresAt(), response.getExpiresDate());
        assertEquals(UserCouponType.AVAILABLE, response.getUserCouponType());

        verify(couponRepository, times(1)).findById(couponId);
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("유저 쿠폰 생성 - 실패 (쿠폰 없음)")
    void saveUserCoupon_couponNotFound() {
        // given
        Long couponId = 100L;
        UserCouponRequest request = new UserCouponRequest(1L, couponId, LocalDateTime.now());

        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CouponNotFoundException.class, () -> userCouponService.saveUserCoupon(request));

        verify(couponRepository, times(1)).findById(couponId);
        verify(userCouponRepository, never()).save(any());
    }

    @Test
    @DisplayName("유저 쿠폰 목록 조회")
    void getUserCoupons_success() {
        // given
        Long userId = 1L;
        Coupon coupon = Coupon.builder()
                .id(200L)
                .name("테스트쿠폰")
                .couponType(CouponType.ORDER)
                .policyType(CouponPolicyType.FIXED)
                .discountAmount(1000)
                .minOrderAmount(Money.of(10000))
                .maxDiscountAmount(Money.of(3000))
                .validDays(10)
                .build();

        UserCoupon userCoupon = new UserCoupon(coupon, userId, LocalDateTime.now().minusDays(1), coupon.getValidDays());
        userCoupon.setId(300L);

        when(userCouponRepository.findByUserId(userId)).thenReturn(List.of(userCoupon));

        // when
        List<UserCouponDetailResponse> responses = userCouponService.getUserCoupons(userId);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());

        UserCouponDetailResponse detail = responses.get(0);
        assertEquals(userCoupon.getId(), detail.getUserCouponId());
        assertEquals(coupon.getName(), detail.getCouponName());

        verify(userCouponRepository, times(1)).findByUserId(userId);
    }



    @Test
    @DisplayName("사용 가능한 책 쿠폰 조회 - 책쿠폰, 카테고리쿠폰 모두 존재")
    void getAvailableCouponsTest() {
        // given
        Long userId = 1L;
        String isbn = "1234567890";
        Set<Long> categoryIds = Set.of(10L, 20L);
        BookInfo bookInfo = new BookInfo(isbn, categoryIds);

        Coupon bookCoupon = Coupon.builder()
                .id(100L)
                .name("책 전용 쿠폰")
                .couponType(CouponType.BOOK)
                .policyType(CouponPolicyType.FIXED)
                .discountAmount(1000)
                .validDays(30)
                .build();

        Coupon categoryCoupon = Coupon.builder()
                .id(200L)
                .name("카테고리 쿠폰")
                .couponType(CouponType.CATEGORY)
                .policyType(CouponPolicyType.RATE)
                .discountAmount(10)
                .validDays(30)
                .build();

        // 필수 필드 세팅
        ReflectionTestUtils.setField(bookCoupon, "minOrderAmount", Money.of(0));
        ReflectionTestUtils.setField(bookCoupon, "maxDiscountAmount", Money.of(1000));
        ReflectionTestUtils.setField(categoryCoupon, "minOrderAmount", Money.of(0));
        ReflectionTestUtils.setField(categoryCoupon, "maxDiscountAmount", Money.of(5000));

        UserCoupon userBookCoupon = new UserCoupon(bookCoupon, userId, LocalDateTime.now(), 30);
        userBookCoupon.setType(UserCouponType.AVAILABLE);
        UserCoupon userCategoryCoupon = new UserCoupon(categoryCoupon, userId, LocalDateTime.now(), 30);
        userCategoryCoupon.setType(UserCouponType.AVAILABLE);

// mocking
        when(userCouponRepository.findByUserId(userId))
                .thenReturn(List.of(userBookCoupon, userCategoryCoupon));

        when(bookCouponRepository.existsByCouponIdAndIsbn(bookCoupon.getId(), isbn)).thenReturn(true);
        when(bookCouponRepository.existsByCouponIdAndIsbn(categoryCoupon.getId(), isbn)).thenReturn(false);

        when(categoryCouponRepository.existsByCouponIdAndCategoryIdIn(categoryCoupon.getId(), categoryIds)).thenReturn(true);
        when(categoryCouponRepository.existsByCouponIdAndCategoryIdIn(bookCoupon.getId(), categoryIds)).thenReturn(false);


        // when
        List<CouponResponse> result = userCouponService.getAvailableCoupons(userId, bookInfo);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(CouponResponse::getName)
                .containsExactlyInAnyOrder("책 전용 쿠폰", "카테고리 쿠폰");

        verify(userCouponRepository).findByUserId(userId);
        verify(bookCouponRepository).existsByCouponIdAndIsbn(bookCoupon.getId(), isbn);
        verify(bookCouponRepository).existsByCouponIdAndIsbn(categoryCoupon.getId(), isbn);

        verify(categoryCouponRepository).existsByCouponIdAndCategoryIdIn(categoryCoupon.getId(), categoryIds);
        verify(categoryCouponRepository).existsByCouponIdAndCategoryIdIn(bookCoupon.getId(), categoryIds);
    }

    @Test
    @DisplayName("사용 가능한 유저 쿠폰 목록 조회 - 성공")
    void getAllAvailableCoupons_success() {
        // given
        Long userId = 1L;
        Coupon validCoupon = createCoupon(1L, "할인쿠폰");



        UserCoupon availableCoupon = spy(createUserCoupon(10L, UserCouponType.AVAILABLE, validCoupon));
        UserCoupon expiredCoupon = spy(createUserCoupon(11L, UserCouponType.EXPIRED, validCoupon));

        List<UserCoupon> userCoupons = List.of(availableCoupon, expiredCoupon);
        when(userCouponRepository.findByUserId(userId)).thenReturn(userCoupons);

        // when
        List<UserCouponDetailResponse> result = userCouponService.getAllAvailableCoupons(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserCouponId()).isEqualTo(10L);
        assertThat(result.get(0).getCouponName()).isEqualTo("할인쿠폰");

        // verify update check
        verify(availableCoupon).updateExpiredCoupon();
        verify(expiredCoupon).updateExpiredCoupon();
    }

    @DisplayName("웰컴 쿠폰 발급 - 성공")
    @Test
    void issueWelcomeCoupon_success() {
        // given
        Long userId = 1L;
        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("신규 회원 가입 축하 쿠폰")
                .validDays(7)
                .build();

        when(couponRepository.findByName("신규 회원 가입 축하 쿠폰")).thenReturn(Optional.of(coupon));

        // when
        UserCouponResponse response = userCouponService.issueWelcomeCoupon(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserCouponType()).isEqualTo(UserCouponType.AVAILABLE);
        assertThat(response.getIssuedAt()).isNotNull();
        assertThat(response.getExpiresDate()).isEqualTo(response.getIssuedAt().plusDays(7));
    }

    @DisplayName("사용 가능한 오더 쿠폰 조회 - 성공")
    @Test
    void getAvailableOrderCoupons_success() {
        // given
        Long userId = 1L;
        Money totalPrice = Money.of(10000);

        Coupon validOrderCoupon1 = Coupon.builder()
                .id(1L)
                .name("오더 쿠폰 1")
                .couponType(CouponType.ORDER)
                .minOrderAmount(Money.of(5000))
                .discountAmount(1000)
                .maxDiscountAmount(Money.of(3000))
                .validDays(7)
                .build();

        Coupon validOrderCoupon2 = Coupon.builder()
                .id(2L)
                .name("오더 쿠폰 2")
                .couponType(CouponType.ORDER)
                .minOrderAmount(Money.of(9000))
                .discountAmount(2000)
                .maxDiscountAmount(Money.of(3000))
                .validDays(7)
                .build();

        // 조건에 안 맞는 쿠폰 (총 금액보다 minOrderAmount가 큼)
        Coupon ineligibleOrderCoupon = Coupon.builder()
                .id(3L)
                .name("오더 쿠폰 3")
                .couponType(CouponType.ORDER)
                .minOrderAmount(Money.of(15000))
                .discountAmount(3000)
                .maxDiscountAmount(Money.of(3000))
                .validDays(7)
                .build();

        // 다른 타입 쿠폰 (BOOK)
        Coupon bookCoupon = Coupon.builder()
                .id(4L)
                .name("북 쿠폰")
                .couponType(CouponType.BOOK)
                .minOrderAmount(Money.of(3000))
                .discountAmount(1000)
                .maxDiscountAmount(Money.of(3000))
                .validDays(7)
                .build();

        UserCoupon available1 = spy(createUserCoupon(10L, UserCouponType.AVAILABLE, validOrderCoupon1));
        UserCoupon available2 = spy(createUserCoupon(11L, UserCouponType.AVAILABLE, validOrderCoupon2));
        UserCoupon notEligible = spy(createUserCoupon(12L, UserCouponType.AVAILABLE, ineligibleOrderCoupon));
        UserCoupon bookType = spy(createUserCoupon(13L, UserCouponType.AVAILABLE, bookCoupon));
        UserCoupon expired = spy(createUserCoupon(14L, UserCouponType.EXPIRED, validOrderCoupon1)); // 상태 EXPIRED

        when(userCouponRepository.findByUserId(userId))
                .thenReturn(List.of(available1, available2, notEligible, bookType, expired));

        // when
        List<CouponResponse> result = userCouponService.getAvailableOrderCoupons(userId, totalPrice);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("id")
                .containsExactlyInAnyOrder(1L, 2L); // 오더 쿠폰 1, 2

        verify(available1).updateExpiredCoupon();
        verify(available2).updateExpiredCoupon();
        verify(notEligible).updateExpiredCoupon();
        verify(bookType).updateExpiredCoupon();
        verify(expired).updateExpiredCoupon();
    }



}
