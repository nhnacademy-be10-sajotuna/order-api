package shop.sajotuna.order.orders.service.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.coupon.domain.Coupon;
import shop.sajotuna.order.coupon.domain.CouponType;
import shop.sajotuna.order.coupon.domain.UserCoupon;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.coupon.repository.UserCouponRepository;
import shop.sajotuna.order.coupon.service.BookCouponValidator;
import shop.sajotuna.order.coupon.service.CategoryCouponValidator;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.exception.PackageNotFoundException;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import shop.sajotuna.order.orders.service.dto.command.CreateOrderProductCommand;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProductCreateServiceTest {

    @Mock
    private OrderPackagingRepository orderPackagingRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private BookCouponValidator bookCouponValidator;

    @Mock
    private CategoryCouponValidator categoryCouponValidator;

    @InjectMocks
    private OrderProductCreateService orderProductCreateService;

    @Test
    @DisplayName("주문 상품들 생성 성공 - 포장과 쿠폰 모두 포함")
    void createOrderProducts_withPackagingAndCoupon() {
        // given
        Long userId = 1L;
        Long packagingId = 1L;
        Long couponId = 1L;
        String isbn = "9781234567890";
        Set<Long> categoryIds = Set.of(1L, 2L);
        
        CreateOrderProductCommand command = mock(CreateOrderProductCommand.class);
        when(command.getPackagingRequest()).thenReturn(true);
        when(command.getOrderPackagingId()).thenReturn(packagingId);
        when(command.getBookCouponId()).thenReturn(couponId);
        when(command.getIsbn()).thenReturn(isbn);
        when(command.getCategoryIds()).thenReturn(categoryIds);
        
        OrderPackaging packaging = mock(OrderPackaging.class);
        UserCoupon userCoupon = mock(UserCoupon.class);
        Coupon coupon = mock(Coupon.class);
        OrderProduct orderProduct = mock(OrderProduct.class);
        
        when(orderPackagingRepository.findById(packagingId)).thenReturn(Optional.of(packaging));
        when(userCouponRepository.findByUserIdAndCouponIdWithCoupon(userId, couponId))
                .thenReturn(Optional.of(userCoupon));
        when(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(true);
        when(userCoupon.getCoupon()).thenReturn(coupon);
        when(coupon.getCouponType()).thenReturn(CouponType.BOOK);
        when(coupon.getId()).thenReturn(couponId);
        when(command.toEntity(packaging, userCoupon)).thenReturn(orderProduct);

        // when
        List<OrderProduct> result = orderProductCreateService.createOrderProducts(List.of(command), userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(orderProduct);
        
        verify(orderPackagingRepository).findById(packagingId);
        verify(userCouponRepository).findByUserIdAndCouponIdWithCoupon(userId, couponId);
        verify(userCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verify(bookCouponValidator).validateCoupon(couponId, isbn);
        verify(command).toEntity(packaging, userCoupon);
    }

    @Test
    @DisplayName("주문 상품들 생성 성공 - 포장만 포함")
    void createOrderProducts_withPackagingOnly() {
        // given
        Long userId = 1L;
        Long packagingId = 1L;
        
        CreateOrderProductCommand command = mock(CreateOrderProductCommand.class);
        when(command.getPackagingRequest()).thenReturn(true);
        when(command.getOrderPackagingId()).thenReturn(packagingId);
        when(command.getBookCouponId()).thenReturn(null);
        
        OrderPackaging packaging = mock(OrderPackaging.class);
        OrderProduct orderProduct = mock(OrderProduct.class);
        
        when(orderPackagingRepository.findById(packagingId)).thenReturn(Optional.of(packaging));
        when(command.toEntity(packaging, null)).thenReturn(orderProduct);

        // when
        List<OrderProduct> result = orderProductCreateService.createOrderProducts(List.of(command), userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(orderProduct);
        
        verify(orderPackagingRepository).findById(packagingId);
        verify(userCouponRepository, never()).findByUserIdAndCouponIdWithCoupon(anyLong(), anyLong());
        verify(command).toEntity(packaging, null);
    }

    @Test
    @DisplayName("주문 상품들 생성 성공 - 카테고리 쿠폰 포함")
    void createOrderProducts_withCategoryCoupon() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        String isbn = "9781234567890";
        Set<Long> categoryIds = Set.of(1L, 2L);
        
        CreateOrderProductCommand command = mock(CreateOrderProductCommand.class);
        when(command.getPackagingRequest()).thenReturn(false);
        when(command.getBookCouponId()).thenReturn(couponId);
        when(command.getIsbn()).thenReturn(isbn);
        when(command.getCategoryIds()).thenReturn(categoryIds);
        
        UserCoupon userCoupon = mock(UserCoupon.class);
        Coupon coupon = mock(Coupon.class);
        OrderProduct orderProduct = mock(OrderProduct.class);
        
        when(userCouponRepository.findByUserIdAndCouponIdWithCoupon(userId, couponId))
                .thenReturn(Optional.of(userCoupon));
        when(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(true);
        when(userCoupon.getCoupon()).thenReturn(coupon);
        when(coupon.getCouponType()).thenReturn(CouponType.CATEGORY);
        when(coupon.getId()).thenReturn(couponId);
        when(command.toEntity(null, userCoupon)).thenReturn(orderProduct);

        // when
        List<OrderProduct> result = orderProductCreateService.createOrderProducts(List.of(command), userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(orderProduct);
        
        verify(userCouponRepository).findByUserIdAndCouponIdWithCoupon(userId, couponId);
        verify(userCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verify(categoryCouponValidator).validateCoupon(couponId, categoryIds);
        verify(bookCouponValidator, never()).validateCoupon(anyLong(), anyString());
        verify(command).toEntity(null, userCoupon);
    }

    @Test
    @DisplayName("주문 상품들 생성 성공 - 포장과 쿠폰 없음")
    void createOrderProducts_withoutPackagingAndCoupon() {
        // given
        Long userId = 1L;
        
        CreateOrderProductCommand command = mock(CreateOrderProductCommand.class);
        when(command.getPackagingRequest()).thenReturn(false);
        when(command.getBookCouponId()).thenReturn(null);
        
        OrderProduct orderProduct = mock(OrderProduct.class);
        when(command.toEntity(null, null)).thenReturn(orderProduct);

        // when
        List<OrderProduct> result = orderProductCreateService.createOrderProducts(List.of(command), userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(orderProduct);
        
        verify(orderPackagingRepository, never()).findById(anyLong());
        verify(userCouponRepository, never()).findByUserIdAndCouponIdWithCoupon(anyLong(), anyLong());
        verify(command).toEntity(null, null);
    }

    @Test
    @DisplayName("주문 상품들 생성 실패 - 포장을 찾을 수 없음")
    void createOrderProducts_packageNotFound() {
        // given
        Long userId = 1L;
        Long packagingId = 999L;
        
        CreateOrderProductCommand command = mock(CreateOrderProductCommand.class);
        when(command.getPackagingRequest()).thenReturn(true);
        when(command.getOrderPackagingId()).thenReturn(packagingId);

        when(orderPackagingRepository.findById(packagingId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderProductCreateService.createOrderProducts(List.of(command), userId))
                .isInstanceOf(PackageNotFoundException.class);
                
        verify(orderPackagingRepository).findById(packagingId);
    }

    @Test
    @DisplayName("주문 상품들 생성 실패 - 쿠폰을 찾을 수 없음")
    void createOrderProducts_couponNotFound() {
        // given
        Long userId = 1L;
        Long couponId = 999L;
        String isbn = "9781234567890";
        Set<Long> categoryIds = Set.of(1L);
        
        CreateOrderProductCommand command = mock(CreateOrderProductCommand.class);
        when(command.getPackagingRequest()).thenReturn(false);
        when(command.getBookCouponId()).thenReturn(couponId);
        when(command.getIsbn()).thenReturn(isbn);
        when(command.getCategoryIds()).thenReturn(categoryIds);
        
        when(userCouponRepository.findByUserIdAndCouponIdWithCoupon(userId, couponId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderProductCreateService.createOrderProducts(List.of(command), userId))
                .isInstanceOf(CouponNotFoundException.class);
                
        verify(userCouponRepository).findByUserIdAndCouponIdWithCoupon(userId, couponId);
    }

    @Test
    @DisplayName("주문 상품들 생성 실패 - 사용자가 쿠폰을 소유하지 않음")
    void createOrderProducts_userDoesNotOwnCoupon() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        String isbn = "9781234567890";
        Set<Long> categoryIds = Set.of(1L);
        
        CreateOrderProductCommand command = mock(CreateOrderProductCommand.class);
        when(command.getPackagingRequest()).thenReturn(false);
        when(command.getBookCouponId()).thenReturn(couponId);
        when(command.getIsbn()).thenReturn(isbn);
        when(command.getCategoryIds()).thenReturn(categoryIds);
        
        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCouponRepository.findByUserIdAndCouponIdWithCoupon(userId, couponId))
                .thenReturn(Optional.of(userCoupon));
        when(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> orderProductCreateService.createOrderProducts(List.of(command), userId))
                .isInstanceOf(CouponNotFoundException.class);
                
        verify(userCouponRepository).findByUserIdAndCouponIdWithCoupon(userId, couponId);
        verify(userCouponRepository).existsByUserIdAndCouponId(userId, couponId);
    }
}