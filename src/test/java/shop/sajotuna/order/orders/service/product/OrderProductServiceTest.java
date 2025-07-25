package shop.sajotuna.order.orders.service.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.controller.dto.response.OrderProductResponse;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.domain.OrderStatus;
import shop.sajotuna.order.orders.exception.OrderProductNotFoundException;
import shop.sajotuna.order.orders.repository.OrderProductRepository;
import shop.sajotuna.order.orders.repository.OrderRepository;
import shop.sajotuna.order.point.exception.OrderNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProductServiceTest {

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderProductService orderProductService;

    @Test
    @DisplayName("주문 상품 조회 성공")
    void findById_success() {
        // given
        Long productId = 1L;
        OrderProduct orderProduct = mock(OrderProduct.class);
        
        when(orderProductRepository.findById(productId)).thenReturn(Optional.of(orderProduct));
        when(orderProduct.getAmount()).thenReturn(Money.of(1000));

        // when
        OrderProductResponse result = orderProductService.findById(productId);

        // then
        assertThat(result).isNotNull();
        verify(orderProductRepository).findById(productId);
    }

    @Test
    @DisplayName("주문 상품 조회 실패 - 상품을 찾을 수 없음")
    void findById_productNotFound() {
        // given
        Long productId = 999L;
        
        when(orderProductRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderProductService.findById(productId))
                .isInstanceOf(OrderProductNotFoundException.class);
                
        verify(orderProductRepository).findById(productId);
    }

    @Test
    @DisplayName("주문ID로 주문 상품들 조회 성공")
    void findByOrderId_success() {
        // given
        Long orderId = 1L;
        OrderProduct orderProduct1 = mock(OrderProduct.class);
        OrderProduct orderProduct2 = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(orderProduct1, orderProduct2);
        
        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderProductRepository.getOrderProductsByOrder_Id(orderId)).thenReturn(orderProducts);
        when(orderProduct1.getAmount()).thenReturn(Money.of(1000));
        when(orderProduct2.getAmount()).thenReturn(Money.of(1000));

        // when
        List<OrderProductResponse> result = orderProductService.findByOrderId(orderId);

        // then
        assertThat(result).hasSize(2);
        verify(orderRepository).existsById(orderId);
        verify(orderProductRepository).getOrderProductsByOrder_Id(orderId);
    }

    @Test
    @DisplayName("주문ID로 주문 상품들 조회 실패 - 주문을 찾을 수 없음")
    void findByOrderId_orderNotFound() {
        // given
        Long orderId = 999L;
        
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> orderProductService.findByOrderId(orderId))
                .isInstanceOf(OrderNotFoundException.class);
                
        verify(orderRepository).existsById(orderId);
        verify(orderProductRepository, never()).getOrderProductsByOrder_Id(anyLong());
    }

    @Test
    @DisplayName("주문ID로 주문 상품들 삭제 성공")
    void deleteByOrderId_success() {
        // given
        Long orderId = 1L;
        
        when(orderRepository.existsById(orderId)).thenReturn(true);

        // when
        orderProductService.deleteByOrderId(orderId);

        // then
        verify(orderRepository).existsById(orderId);
        verify(orderProductRepository).deleteByOrder_Id(orderId);
    }

    @Test
    @DisplayName("주문ID로 주문 상품들 삭제 실패 - 주문을 찾을 수 없음")
    void deleteByOrderId_orderNotFound() {
        // given
        Long orderId = 999L;
        
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> orderProductService.deleteByOrderId(orderId))
                .isInstanceOf(OrderNotFoundException.class);
                
        verify(orderRepository).existsById(orderId);
        verify(orderProductRepository, never()).deleteByOrder_Id(anyLong());
    }

    @Test
    @DisplayName("리뷰 작성 가능 여부 확인 - 가능한 경우")
    void isEligibleForReview_eligible() {
        // given
        Long userId = 1L;
        String isbn = "9781234567890";
        
        when(orderProductRepository.existsByOrderOrdererUserIdAndIsbnAndOrderStatus(userId, isbn, OrderStatus.DELIVERED))
                .thenReturn(true);

        // when
        boolean result = orderProductService.isEligibleForReview(userId, isbn);

        // then
        assertThat(result).isTrue();
        verify(orderProductRepository).existsByOrderOrdererUserIdAndIsbnAndOrderStatus(userId, isbn, OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("리뷰 작성 가능 여부 확인 - 불가능한 경우")
    void isEligibleForReview_notEligible() {
        // given
        Long userId = 1L;
        String isbn = "9781234567890";
        
        when(orderProductRepository.existsByOrderOrdererUserIdAndIsbnAndOrderStatus(userId, isbn, OrderStatus.DELIVERED))
                .thenReturn(false);

        // when
        boolean result = orderProductService.isEligibleForReview(userId, isbn);

        // then
        assertThat(result).isFalse();
        verify(orderProductRepository).existsByOrderOrdererUserIdAndIsbnAndOrderStatus(userId, isbn, OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("리뷰 작성 가능 여부 확인 - userId가 null인 경우")
    void isEligibleForReview_nullUserId() {
        // given
        Long userId = null;
        String isbn = "9781234567890";

        // when
        boolean result = orderProductService.isEligibleForReview(userId, isbn);

        // then
        assertThat(result).isFalse();
        verify(orderProductRepository, never()).existsByOrderOrdererUserIdAndIsbnAndOrderStatus(anyLong(), anyString(), any(OrderStatus.class));
    }

    @Test
    @DisplayName("리뷰 작성 가능 여부 확인 - isbn이 null인 경우")
    void isEligibleForReview_nullIsbn() {
        // given
        Long userId = 1L;
        String isbn = null;

        // when
        boolean result = orderProductService.isEligibleForReview(userId, isbn);

        // then
        assertThat(result).isFalse();
        verify(orderProductRepository, never()).existsByOrderOrdererUserIdAndIsbnAndOrderStatus(anyLong(), anyString(), any(OrderStatus.class));
    }

    @Test
    @DisplayName("리뷰 작성 가능 여부 확인 - isbn이 빈 문자열인 경우")
    void isEligibleForReview_emptyIsbn() {
        // given
        Long userId = 1L;
        String isbn = "  ";

        // when
        boolean result = orderProductService.isEligibleForReview(userId, isbn);

        // then
        assertThat(result).isFalse();
        verify(orderProductRepository, never()).existsByOrderOrdererUserIdAndIsbnAndOrderStatus(anyLong(), anyString(), any(OrderStatus.class));
    }
}