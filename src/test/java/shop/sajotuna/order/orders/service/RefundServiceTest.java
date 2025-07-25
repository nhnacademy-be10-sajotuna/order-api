package shop.sajotuna.order.orders.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.orders.domain.Discounts;
import shop.sajotuna.order.orders.domain.Order;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.stock.service.StockService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private StockService stockService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private RefundService refundService;

    @Test
    @DisplayName("반품시 쿠폰이 반환된다")
    void returnCoupon() {
        // given
        Order mockOrder = mock(Order.class);
        OrderProduct product1 = createOrderProduct("1234567890", 1, mock(UserCoupon.class));
        OrderProduct product2 = createOrderProduct("0987654321", 2, null);
        Discounts discounts = new Discounts(Money.of(1000), Money.of(1000), 1L);

        when(mockOrder.getOrderProducts()).thenReturn(List.of(product1, product2));
        when(mockOrder.getDiscounts()).thenReturn(discounts);

        UserCoupon mockUserCoupon = mock(UserCoupon.class);
        doNothing().when(mockUserCoupon).returnCoupon();
        when(userCouponRepository.findById(discounts.getUsedCouponId())).thenReturn(Optional.ofNullable(mockUserCoupon));

        // when
        refundService.returnCoupon(mockOrder);

        // then
        verify(product1.getAppliedCoupon(), times(1)).returnCoupon();
        verify(mockUserCoupon, times(1)).returnCoupon();
    }

    @Test
    @DisplayName("반품시 재고가 복구된다")
    void returnStock() {
        // given
        Order mockOrder = mock(Order.class);
        OrderProduct product1 = createOrderProduct("1234567890", 1, null);
        OrderProduct product2 = createOrderProduct("0987654321", 2, null);

        when(mockOrder.getOrderProducts()).thenReturn(List.of(product1, product2));
        doNothing().when(stockService).increaseStock(anyString(), anyInt());

        // when
        refundService.returnStock(mockOrder);

        // then
        verify(stockService, times(1)).increaseStock("1234567890", 1);
        verify(stockService, times(1)).increaseStock("0987654321", 2);
    }

    private static OrderProduct createOrderProduct(String number, int qty, UserCoupon coupon) {
        return OrderProduct.builder()
                .isbn(number)
                .qty(qty)
                .appliedCoupon(coupon)
                .build();
    }

}