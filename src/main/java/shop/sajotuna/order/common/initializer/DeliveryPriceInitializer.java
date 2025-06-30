package shop.sajotuna.order.common.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.orders.domain.DeliveryPrice;
import shop.sajotuna.order.orders.repository.DeliveryPriceRepository;

@Component
@RequiredArgsConstructor
public class DeliveryPriceInitializer implements ApplicationRunner {

    private final DeliveryPriceRepository deliveryPriceRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (deliveryPriceRepository.count() == 0) {
            DeliveryPrice deliveryPrice = new DeliveryPrice(null, Money.of(30000), Money.of(5000));
            deliveryPriceRepository.save(deliveryPrice);
        }
    }
}
