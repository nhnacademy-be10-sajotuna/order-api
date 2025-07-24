package shop.sajotuna.order.orders.service;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.controller.dto.response.DeliveryPriceResponse;
import shop.sajotuna.order.orders.controller.dto.response.OrderFormResponse;
import shop.sajotuna.order.orders.controller.dto.response.PackageResponse;
import shop.sajotuna.order.orders.domain.DeliveryPrice;
import shop.sajotuna.order.orders.domain.OrderPackaging;
import shop.sajotuna.order.orders.repository.DeliveryPriceRepository;
import shop.sajotuna.order.orders.repository.OrderPackagingRepository;
import shop.sajotuna.order.orders.service.product.PackageService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderFormServiceTest {

    @Autowired
    private OrderFormService orderFormService;
    @Autowired
    private DeliveryPriceRepository deliveryPriceRepository;
    @Autowired
    private OrderPackagingRepository orderPackagingRepository;

    @Test
    @DisplayName("비회원 주문 폼 조회")
    void getGuestOrderForm() {
        // given
        DeliveryPrice deliveryPrice = new DeliveryPrice(null, Money.of(30000), Money.of(5000));
        deliveryPriceRepository.save(deliveryPrice);
        OrderPackaging orderPackaging = new OrderPackaging("기본 포장", Money.of(1000));
        orderPackagingRepository.save(orderPackaging);

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

        // when

        // then
    }
}