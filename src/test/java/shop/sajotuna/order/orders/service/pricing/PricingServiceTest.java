package shop.sajotuna.order.orders.service.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.DeliveryPrice;
import shop.sajotuna.order.orders.domain.OrderProduct;
import shop.sajotuna.order.orders.domain.OrderPrice;
import shop.sajotuna.order.orders.repository.DeliveryPriceRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock
    private DeliveryPriceRepository deliveryPriceRepository;

    @InjectMocks
    private PricingService pricingService;

    @Test
    @DisplayName("주문 가격 계산 성공 - 여러 상품")
    void calculatePrices_multipleProducts() {
        // given
        OrderProduct product1 = mock(OrderProduct.class);
        OrderProduct product2 = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(product1, product2);
        
        when(product1.getTotalPrice()).thenReturn(Money.of(10000));
        when(product1.getPackagingPrice()).thenReturn(Money.of(500));
        when(product2.getTotalPrice()).thenReturn(Money.of(15000));
        when(product2.getPackagingPrice()).thenReturn(Money.of(1000));
        
        DeliveryPrice deliveryPricePolicy = mock(DeliveryPrice.class);
        when(deliveryPriceRepository.getDefaultDeliveryPrice()).thenReturn(deliveryPricePolicy);
        when(deliveryPricePolicy.calculateDeliveryPrice(Money.of(25000))).thenReturn(Money.of(3000));

        // when
        OrderPrice result = pricingService.calculatePrices(orderProducts);

        // then
        assertThat(result.getTotalProductPrice()).isEqualTo(Money.of(25000));
        assertThat(result.getPackagingPrice()).isEqualTo(Money.of(1500));
        assertThat(result.getDeliveryPrice()).isEqualTo(Money.of(3000));
        
        verify(deliveryPriceRepository).getDefaultDeliveryPrice();
        verify(deliveryPricePolicy).calculateDeliveryPrice(Money.of(25000));
    }

    @Test
    @DisplayName("주문 가격 계산 성공 - 단일 상품")
    void calculatePrices_singleProduct() {
        // given
        OrderProduct product = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(product);
        
        when(product.getTotalPrice()).thenReturn(Money.of(20000));
        when(product.getPackagingPrice()).thenReturn(Money.of(0));
        
        DeliveryPrice deliveryPricePolicy = mock(DeliveryPrice.class);
        when(deliveryPriceRepository.getDefaultDeliveryPrice()).thenReturn(deliveryPricePolicy);
        when(deliveryPricePolicy.calculateDeliveryPrice(Money.of(20000))).thenReturn(Money.of(2500));

        // when
        OrderPrice result = pricingService.calculatePrices(orderProducts);

        // then
        assertThat(result.getTotalProductPrice()).isEqualTo(Money.of(20000));
        assertThat(result.getPackagingPrice()).isEqualTo(Money.zero());
        assertThat(result.getDeliveryPrice()).isEqualTo(Money.of(2500));
        
        verify(deliveryPriceRepository).getDefaultDeliveryPrice();
        verify(deliveryPricePolicy).calculateDeliveryPrice(Money.of(20000));
    }

    @Test
    @DisplayName("주문 가격 계산 성공 - 포장 없는 상품들")
    void calculatePrices_noPackaging() {
        // given
        OrderProduct product1 = mock(OrderProduct.class);
        OrderProduct product2 = mock(OrderProduct.class);
        List<OrderProduct> orderProducts = List.of(product1, product2);
        
        when(product1.getTotalPrice()).thenReturn(Money.of(8000));
        when(product1.getPackagingPrice()).thenReturn(Money.zero());
        when(product2.getTotalPrice()).thenReturn(Money.of(12000));
        when(product2.getPackagingPrice()).thenReturn(Money.zero());
        
        DeliveryPrice deliveryPricePolicy = mock(DeliveryPrice.class);
        when(deliveryPriceRepository.getDefaultDeliveryPrice()).thenReturn(deliveryPricePolicy);
        when(deliveryPricePolicy.calculateDeliveryPrice(Money.of(20000))).thenReturn(Money.zero());

        // when
        OrderPrice result = pricingService.calculatePrices(orderProducts);

        // then
        assertThat(result.getTotalProductPrice()).isEqualTo(Money.of(20000));
        assertThat(result.getPackagingPrice()).isEqualTo(Money.zero());
        assertThat(result.getDeliveryPrice()).isEqualTo(Money.zero());
        
        verify(deliveryPriceRepository).getDefaultDeliveryPrice();
        verify(deliveryPricePolicy).calculateDeliveryPrice(Money.of(20000));
    }
}