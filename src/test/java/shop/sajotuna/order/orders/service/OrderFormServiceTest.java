package shop.sajotuna.order.orders.service;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.*;
import shop.sajotuna.order.coupon.repository.CouponRepository;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.orders.controller.dto.response.DeliveryPriceResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderFormResponse;
import shop.sajotuna.order.orders.controller.dto.response.PackageResponse;
import shop.sajotuna.order.orders.domain.DeliveryPrice;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.repository.DeliveryPriceRepository;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import shop.sajotuna.order.orders.service.product.PackageService;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.repository.UserPointRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderFormServiceTest {

    @Autowired
    private OrderFormService orderFormService;
    @Autowired
    private DeliveryPriceRepository deliveryPriceRepository;
    @Autowired
    private OrderPackagingRepository orderPackagingRepository;
    @Autowired
    private UserPointRepository userPointRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 데이터 정리
        userCouponRepository.deleteAll();
        couponRepository.deleteAll();
        userPointRepository.deleteAll();
        orderPackagingRepository.deleteAll();
        deliveryPriceRepository.deleteAll();
        
        // Default DeliveryPrice를 매번 생성 (ID=1) - 네이티브 쿼리 사용
        entityManager.createNativeQuery(
            "INSERT INTO delivery_price (id, free_delivery_min_price, delivery_price) VALUES (1, 30000, 5000)")
            .executeUpdate();
        entityManager.flush();
    }

    @Test
    @DisplayName("비회원 주문 폼 조회")
    void getGuestOrderForm() {
        // given
        OrderPackaging orderPackaging = new OrderPackaging("기본 포장", Money.of(1000));
        orderPackaging = orderPackagingRepository.saveAndFlush(orderPackaging);
        DeliveryPrice deliveryPrice = deliveryPriceRepository.getDefaultDeliveryPrice();

        // when
        OrderFormResponse orderForm = orderFormService.getOrderForm(null);

        // then
        assertThat(orderForm.getPackages())
                .extracting("packaging", "price")
                .containsExactly(tuple(orderPackaging.getPackaging(), orderPackaging.getPrice().getAmount()));
        assertThat(orderForm.getDeliveryPrice().getDeliveryPrice()).isEqualTo(deliveryPrice.getDeliveryPrice().getAmount());
        assertThat(orderForm.getDeliveryPrice().getFreeDeliveryMinPrice()).isEqualTo(deliveryPrice.getFreeDeliveryMinPrice().getAmount());
    }

    @Test
    @DisplayName("회원 주문 폼 조회")
    void getUserOrderForm() {
        // given
        Long userId = 1L;
        
        // 배송비는 setUp에서 이미 생성됨
        DeliveryPrice deliveryPrice = deliveryPriceRepository.getDefaultDeliveryPrice();
        
        // 포장 설정
        OrderPackaging orderPackaging = new OrderPackaging("기본 포장", Money.of(1000));
        orderPackaging = orderPackagingRepository.saveAndFlush(orderPackaging);
        
        // 사용자 포인트 설정
        UserPoint userPoint = UserPoint.create(userId);
        userPoint.earnPoint(Money.of(2500));
        userPointRepository.save(userPoint);
        
        // 쿠폰 설정
        Coupon coupon = Coupon.builder()
                .name("생일 축하 쿠폰")
                .couponType(CouponType.ORDER)
                .policyType(CouponPolicyType.FIXED)
                .discountAmount(5000)
                .minOrderAmount(Money.of(10000))
                .maxDiscountAmount(Money.of(10000))
                .validDays(30)
                .build();
        couponRepository.save(coupon);
        
        // 사용자 쿠폰 설정
        UserCoupon userCoupon = new UserCoupon(coupon, userId, LocalDateTime.now(), 30);
        userCouponRepository.save(userCoupon);

        // when
        OrderFormResponse orderForm = orderFormService.getOrderForm(userId);

        // then
        assertThat(orderForm.getPoint()).isEqualTo(2500);
        assertThat(orderForm.getCoupons()).hasSize(1)
                .extracting("couponName", "discountAmount", "minOrderAmount")
                .containsExactly(tuple("생일 축하 쿠폰", 5000, 10000));
        assertThat(orderForm.getPackages())
                .extracting("packaging", "price")
                .containsExactly(tuple(orderPackaging.getPackaging(), orderPackaging.getPrice().getAmount()));
        assertThat(orderForm.getDeliveryPrice().getDeliveryPrice()).isEqualTo(deliveryPrice.getDeliveryPrice().getAmount());
        assertThat(orderForm.getDeliveryPrice().getFreeDeliveryMinPrice()).isEqualTo(deliveryPrice.getFreeDeliveryMinPrice().getAmount());
    }
}