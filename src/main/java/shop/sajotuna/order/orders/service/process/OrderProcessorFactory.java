package shop.sajotuna.order.orders.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProcessorFactory {
    private final UserOrderProcessor userOrderProcessor;
    private final GuestOrderProcessor guestOrderProcessor;

    // TODO: 새로운 유저 타입에 대응할 수 있도록 확장 가능하게 변경
    public OrderProcessor getOrderProcessor(Long userId) {
        if (userId != null) {
            return userOrderProcessor;
        } else {
            return guestOrderProcessor;
        }
    }
}
