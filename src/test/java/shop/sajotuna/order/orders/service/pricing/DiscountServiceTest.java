package shop.sajotuna.order.orders.service.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.point.service.PointService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private PointService pointService;

    @InjectMocks
    private DiscountService discountService;

    @Test
    @DisplayName("할인 적용 성공 - 쿠폰과 포인트 모두 사용")
    void applyDiscountsToProducts_withCouponAndPoint() {
        // given
        Long userId = 1L;
        Long orderCouponId = 1L;
        Money usedPoint = Money.of(1000);
        
        OrderProduct orderProduct1 = mock(OrderProduct.class);
        OrderProduct orderProduct2 = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(orderProduct1, orderProduct2);
        
        UserCoupon orderCoupon = mock(UserCoupon.class);
        
        when(orderProduct1.applyCouponDiscount()).thenReturn(Money.of(500));
        when(orderProduct2.applyCouponDiscount()).thenReturn(Money.of(300));
        when(orderProduct1.getTotalPrice()).thenReturn(Money.of(10000));
        when(orderProduct2.getTotalPrice()).thenReturn(Money.of(15000));
        
        when(userCouponRepository.findByIdWithCoupon(orderCouponId))
                .thenReturn(Optional.of(orderCoupon));
        when(orderCoupon.applyCoupon(Money.of(24200))).thenReturn(Money.of(2000));

        // when
        Discounts result = discountService.applyDiscountsToProducts(orderCouponId, usedPoint, userId, orderProducts);

        // then
        assertThat(result.getCouponDiscountAmount()).isEqualTo(Money.of(2800)); // 500 + 300 + 2000
        assertThat(result.getUsedPoint()).isEqualTo(Money.of(1000));
        assertThat(result.getUsedCouponId()).isEqualTo(orderCouponId);
        
        verify(pointService).redeemPoints(userId, usedPoint);
    }

    @Test
    @DisplayName("할인 적용 성공 - 쿠폰만 사용")
    void applyDiscountsToProducts_withCouponOnly() {
        // given
        Long userId = 1L;
        Long orderCouponId = 1L;
        Money usedPoint = Money.zero();
        
        OrderProduct orderProduct = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(orderProduct);
        
        UserCoupon orderCoupon = mock(UserCoupon.class);
        
        when(orderProduct.applyCouponDiscount()).thenReturn(Money.of(500));
        when(orderProduct.getTotalPrice()).thenReturn(Money.of(10000));
        
        when(userCouponRepository.findByIdWithCoupon(orderCouponId))
                .thenReturn(Optional.of(orderCoupon));
        when(orderCoupon.applyCoupon(Money.of(9500))).thenReturn(Money.of(1000));

        // when
        Discounts result = discountService.applyDiscountsToProducts(orderCouponId, usedPoint, userId, orderProducts);

        // then
        assertThat(result.getCouponDiscountAmount()).isEqualTo(Money.of(1500)); // 500 + 1000
        assertThat(result.getUsedPoint()).isEqualTo(Money.zero());
        assertThat(result.getUsedCouponId()).isEqualTo(orderCouponId);
        
        verify(pointService, never()).redeemPoints(anyLong(), any(Money.class));
    }

    @Test
    @DisplayName("할인 적용 성공 - 포인트만 사용")
    void applyDiscountsToProducts_withPointOnly() {
        // given
        Long userId = 1L;
        Long orderCouponId = null;
        Money usedPoint = Money.of(2000);
        
        OrderProduct orderProduct = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(orderProduct);
        
        when(orderProduct.applyCouponDiscount()).thenReturn(Money.of(300));

        // when
        Discounts result = discountService.applyDiscountsToProducts(orderCouponId, usedPoint, userId, orderProducts);

        // then
        assertThat(result.getCouponDiscountAmount()).isEqualTo(Money.of(300));
        assertThat(result.getUsedPoint()).isEqualTo(Money.of(2000));
        assertThat(result.getUsedCouponId()).isNull();
        
        verify(pointService).redeemPoints(userId, usedPoint);
        verify(userCouponRepository, never()).findByIdWithCoupon(anyLong());
    }

    @Test
    @DisplayName("할인 적용 성공 - 할인 없음")
    void applyDiscountsToProducts_noDiscount() {
        // given
        Long userId = 1L;
        Long orderCouponId = null;
        Money usedPoint = Money.zero();
        
        OrderProduct orderProduct = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(orderProduct);
        
        when(orderProduct.applyCouponDiscount()).thenReturn(Money.zero());

        // when
        Discounts result = discountService.applyDiscountsToProducts(orderCouponId, usedPoint, userId, orderProducts);

        // then
        assertThat(result.getCouponDiscountAmount()).isEqualTo(Money.zero());
        assertThat(result.getUsedPoint()).isEqualTo(Money.zero());
        assertThat(result.getUsedCouponId()).isNull();
        
        verify(pointService, never()).redeemPoints(anyLong(), any(Money.class));
        verify(userCouponRepository, never()).findByIdWithCoupon(anyLong());
    }

    @Test
    @DisplayName("할인 적용 실패 - 쿠폰을 찾을 수 없음")
    void applyDiscountsToProducts_couponNotFound() {
        // given
        Long userId = 1L;
        Long orderCouponId = 999L;
        Money usedPoint = Money.zero();
        
        OrderProduct orderProduct = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(orderProduct);
        
        when(orderProduct.applyCouponDiscount()).thenReturn(Money.zero());
        when(orderProduct.getTotalPrice()).thenReturn(Money.of(10000));
        when(userCouponRepository.findByIdWithCoupon(orderCouponId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> discountService.applyDiscountsToProducts(orderCouponId, usedPoint, userId, orderProducts))
                .isInstanceOf(CouponNotFoundException.class);
                
        verify(pointService, never()).redeemPoints(anyLong(), any(Money.class));
    }
}