package shop.sajotuna.order.coupon.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponPolicyType;
import shop.sajotuna.order.coupon.domain.CouponSpecificBook;
import shop.sajotuna.order.coupon.domain.CouponSpecificCategory;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.dto.request.BookInfo;
import shop.sajotuna.order.coupon.dto.response.CouponResponse;
import shop.sajotuna.order.coupon.repository.BookCouponRepository;
import shop.sajotuna.order.coupon.repository.CategoryCouponRepository;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserCouponServiceQueryTest {

    private static final Long USER_ID = 1L;
    private static final String MATCHING_ISBN = "9788966263158";

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private BookCouponRepository bookCouponRepository;

    @Autowired
    private CategoryCouponRepository categoryCouponRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        userCouponRepository.deleteAll();
        bookCouponRepository.deleteAll();
        categoryCouponRepository.deleteAll();
        couponRepository.deleteAll();

        statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
    }

    @Test
    @DisplayName("사용 가능한 도서/카테고리 쿠폰 조회는 보유 쿠폰 수와 무관하게 고정 쿼리로 판별한다")
    void getAvailableCoupons_usesConstantQueryCount() {
        Coupon bookCoupon = saveCoupon("book coupon", CouponType.BOOK);
        Coupon categoryCoupon = saveCoupon("category coupon", CouponType.CATEGORY);
        Coupon unmatchedCoupon1 = saveCoupon("unmatched coupon 1", CouponType.BOOK);
        Coupon unmatchedCoupon2 = saveCoupon("unmatched coupon 2", CouponType.CATEGORY);

        userCouponRepository.save(new UserCoupon(bookCoupon, USER_ID, LocalDateTime.now(), 30));
        userCouponRepository.save(new UserCoupon(categoryCoupon, USER_ID, LocalDateTime.now(), 30));
        userCouponRepository.save(new UserCoupon(unmatchedCoupon1, USER_ID, LocalDateTime.now(), 30));
        userCouponRepository.save(new UserCoupon(unmatchedCoupon2, USER_ID, LocalDateTime.now(), 30));

        bookCouponRepository.save(new CouponSpecificBook(MATCHING_ISBN, bookCoupon));
        bookCouponRepository.save(new CouponSpecificBook("9780000000000", unmatchedCoupon1));
        categoryCouponRepository.save(new CouponSpecificCategory(10L, categoryCoupon));
        categoryCouponRepository.save(new CouponSpecificCategory(99L, unmatchedCoupon2));

        entityManager.flush();
        entityManager.clear();
        statistics.clear();

        List<CouponResponse> result = userCouponService.getAvailableCoupons(
                USER_ID,
                new BookInfo(MATCHING_ISBN, Set.of(10L, 20L))
        );

        assertThat(result)
                .extracting(CouponResponse::getName)
                .containsExactlyInAnyOrder("book coupon", "category coupon");
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(3);
    }

    private Coupon saveCoupon(String name, CouponType couponType) {
        return couponRepository.save(Coupon.builder()
                .name(name)
                .couponType(couponType)
                .policyType(CouponPolicyType.FIXED)
                .discountAmount(1_000)
                .minOrderAmount(Money.zero())
                .maxDiscountAmount(Money.of(1_000))
                .validDays(30)
                .build());
    }
}
