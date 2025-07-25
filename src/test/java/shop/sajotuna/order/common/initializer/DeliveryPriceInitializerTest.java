package shop.sajotuna.order.common.initializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.DeliveryPrice;
import shop.sajotuna.order.orders.repository.DeliveryPriceRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryPriceInitializerTest {

    @Mock
    private DeliveryPriceRepository deliveryPriceRepository;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private DeliveryPriceInitializer deliveryPriceInitializer;

    @Test
    @DisplayName("배송비 데이터가 없을 때 기본 배송비를 생성한다")
    void createDefaultDeliveryPriceWhenEmpty() throws Exception {
        // given
        when(deliveryPriceRepository.count()).thenReturn(0L);

        // when
        deliveryPriceInitializer.run(applicationArguments);

        // then
        verify(deliveryPriceRepository).save(argThat(deliveryPrice -> 
            deliveryPrice.getFreeDeliveryMinPrice().equals(Money.of(30000)) &&
            deliveryPrice.getDeliveryPrice().equals(Money.of(5000))
        ));
    }

    @Test
    @DisplayName("배송비 데이터가 이미 존재할 때 추가로 생성하지 않는다")
    void doNotCreateWhenDeliveryPriceExists() throws Exception {
        // given
        when(deliveryPriceRepository.count()).thenReturn(1L);

        // when
        deliveryPriceInitializer.run(applicationArguments);

        // then
        verify(deliveryPriceRepository, never()).save(any(DeliveryPrice.class));
    }
}