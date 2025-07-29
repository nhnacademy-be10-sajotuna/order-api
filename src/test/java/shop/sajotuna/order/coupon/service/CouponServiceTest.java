package shop.sajotuna.order.coupon.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.*;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.request.CouponRequest;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.repository.BookCouponRepository;
import shop.sajotuna.order.coupon.repository.CategoryCouponRepository;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private CategoryCouponRepository categoryCouponRepository;

    @Mock
    private BookCouponRepository bookCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        couponRepository = mock(CouponRepository.class);
        bookCouponRepository = mock(BookCouponRepository.class);
        categoryCouponRepository = mock(CategoryCouponRepository.class);

        couponService = new CouponService(
                couponRepository,
                bookCouponRepository,
                categoryCouponRepository
        );
    }

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

    @Test
    @DisplayName("쿠폰 생성 테스트")
    void couponSaveTest(){
        // given
        CouponRequest couponRequest = new CouponRequest(
                "할인쿠폰",
                CouponType.ORDER,
                CouponPolicyType.FIXED,
                3000,
                20000,
                5000
                ,7
        );
        Coupon expectedCoupon = couponRequest.toEntity();

        when(couponRepository.save(any(Coupon.class))).thenReturn(expectedCoupon);


        // when
        CouponResponse response = couponService.saveCoupon(couponRequest);

        // then
        assertNotNull(response);
        assertEquals("할인쿠폰", response.getName());
        assertEquals(CouponType.ORDER, response.getCouponType());
        assertEquals(CouponPolicyType.FIXED, response.getPolicyType());
        assertEquals(3000, response.getDiscountAmount());
        assertEquals(20000, response.getMinOrderAmount());
        assertEquals(5000, response.getMaxDiscountAmount());
        assertEquals(7, response.getValidDays());

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    @DisplayName("쿠폰 삭제 테스트")
    void deleteCouponTest(){
        Long couponId = 1L;

        when(couponRepository.existsById(couponId)).thenReturn(true);
        when(bookCouponRepository.existsByCoupon_Id(couponId)).thenReturn(true);
        when(categoryCouponRepository.existsByCoupon_Id(couponId)).thenReturn(true);

        couponService.deleteCoupon(couponId);

        verify(bookCouponRepository, times(1)).deleteByCoupon_Id(couponId);
        verify(categoryCouponRepository, times(1)).deleteByCoupon_Id(couponId);
        verify(couponRepository, times(1)).deleteById(couponId);

    }

    @Test
    @DisplayName("쿠폰 삭제 - 쿠폰이 존재하지 않을 때 예외 발생")
    void deleteCoupon_NotFound_ThrowsException() {
        Long couponId = 1L;

        when(couponRepository.existsById(couponId)).thenReturn(false);

        // 예외 발생 검증
        assertThrows(EntityNotFoundException.class, () -> couponService.deleteCoupon(couponId));

        // 삭제 메서드가 호출되지 않았는지 검증
        verify(bookCouponRepository, never()).deleteByCoupon_Id(anyLong());
        verify(categoryCouponRepository, never()).deleteByCoupon_Id(anyLong());
        verify(couponRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("책 쿠폰 + 카테고리 쿠폰 조회 테스트")
    void findBookCouponsTest() {
        // given
        String isbn = "123-4567890123";
        Set<Long> categoryIds = Set.of(1L, 2L, 3L);
        BookInfo bookInfo = new BookInfo(isbn, categoryIds);

        Coupon coupon1 = Coupon.builder()
                .id(1L)
                .name("책쿠폰1")
                .couponType(CouponType.BOOK)
                .policyType(CouponPolicyType.FIXED)
                .discountAmount(1000)
                .minOrderAmount(Money.of(1000))
                .maxDiscountAmount(Money.of(5000))
                .validDays(10)
                .build();

        Coupon coupon2 = Coupon.builder()
                .id(2L)
                .name("카테고리쿠폰1")
                .couponType(CouponType.CATEGORY)
                .policyType(CouponPolicyType.RATE)
                .discountAmount(10)
                .minOrderAmount(Money.of(0))
                .maxDiscountAmount(Money.of(10000))
                .validDays(5)
                .build();

        CouponSpecificBook bookCoupon = new CouponSpecificBook(isbn, coupon1);
        CouponSpecificCategory categoryCoupon = new CouponSpecificCategory(categoryIds.iterator().next(), coupon2);

        when(bookCouponRepository.findByIsbn(isbn)).thenReturn(List.of(bookCoupon));
        when(categoryCouponRepository.findByCategoryIdIn(categoryIds)).thenReturn(List.of(categoryCoupon));

        // when
        List<CouponResponse> result = couponService.findBookCoupons(bookInfo);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());

        // 쿠폰 이름으로 포함 여부 확인
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("책쿠폰1")));
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("카테고리쿠폰1")));

        verify(bookCouponRepository, times(1)).findByIsbn(isbn);
        verify(categoryCouponRepository, times(1)).findByCategoryIdIn(categoryIds);
    }

    @Test
    @DisplayName("책 쿠폰 저장 성공")
    void saveBookCoupon_success() {
        // given
        CouponRequest couponRequest = new CouponRequest(
                "할인쿠폰",
                CouponType.ORDER,
                CouponPolicyType.FIXED,
                3000,
                20000,
                5000
                ,7
        );
        Coupon coupon = couponRequest.toEntity();

        when(couponRepository.save(any())).thenReturn(coupon);

        // when
        CouponResponse result = couponService.saveBookCoupon("1234567890", couponRequest);

        // then
        assertThat(result.getName()).isEqualTo(couponRequest.getName());
        verify(couponRepository).save(any());
        verify(bookCouponRepository).save(any(CouponSpecificBook.class));
    }

    @Test
    @DisplayName("카테고리 쿠폰 저장 성공")
    void saveCategoryCoupon_success() {
        // given
        CouponRequest couponRequest = new CouponRequest(
                "할인쿠폰",
                CouponType.ORDER,
                CouponPolicyType.FIXED,
                3000,
                20000,
                5000
                ,7
        );
        Coupon coupon = couponRequest.toEntity();

        when(couponRepository.save(any())).thenReturn(coupon);

        // when
        CouponResponse result = couponService.saveCategoryCoupon(10L, couponRequest);

        // then
        assertThat(result.getName()).isEqualTo(couponRequest.getName());
        verify(couponRepository).save(any());
        verify(categoryCouponRepository).save(any(CouponSpecificCategory.class));
    }

    @Test
    @DisplayName("모든 쿠폰 목록 조회")
    void getAllCoupons_success() {
        // given
        List<Coupon> coupons = List.of(
                createCoupon(1L, "쿠폰 A"),
                createCoupon(2L, "쿠폰 B")
        );
        when(couponRepository.findAll()).thenReturn(coupons);

        // when
        List<CouponResponse> responses = couponService.getAllCoupons();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("쿠폰 A");
        assertThat(responses.get(1).getName()).isEqualTo("쿠폰 B");
    }

    @Test
    @DisplayName("쿠폰 단건 조회 성공")
    void findCoupon_success() {
        // given
        Coupon coupon = createCoupon(10L, "단건 쿠폰");
        when(couponRepository.findById(10L)).thenReturn(Optional.of(coupon));

        // when
        CouponResponse result = couponService.findCoupon(10L);

        // then
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("단건 쿠폰");
    }

    @Test
    @DisplayName("쿠폰 정보 수정 성공")
    void editCoupon_success() {
        // given
        Long couponId = 1L;
        Coupon coupon = spy(createCoupon(couponId, "기존 쿠폰"));

        CouponRequest request = new CouponRequest(
                "수정된 쿠폰",
                CouponType.ORDER,
                CouponPolicyType.FIXED,
                3000,
                5000,
                10000,
                15
        );

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        couponService.editCoupon(couponId, request);

        // then
        verify(coupon).updateCoupon(request);
    }
}
