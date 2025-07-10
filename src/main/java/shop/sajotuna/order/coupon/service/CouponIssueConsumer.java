package shop.sajotuna.order.coupon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import shop.sajotuna.order.coupon.dto.request.UserCouponRequest;
import shop.sajotuna.order.coupon.service.dto.event.CouponEvent;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueConsumer {
    private final UserCouponService userCouponService;

    @RabbitListener(queues = "${rabbitmq.coupon.queue}", containerFactory = "couponListenerContainerFactory")
    @Transactional
    public void onMessage(CouponEvent event) {
        log.info("Received Coupon Issue Event: userId={}, couponId={}", event.getUserId(), event.getCouponId());

        UserCouponRequest request = new UserCouponRequest(
                event.getUserId(),
                event.getCouponId(),
                LocalDateTime.now()
        );

        userCouponService.saveUserCoupon(request);
    }

}
