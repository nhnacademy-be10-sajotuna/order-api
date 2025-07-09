package shop.sajotuna.order.coupon.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.coupon")
@AllArgsConstructor
@Getter
public class CouponRabbitProperties {
    private String queue;
    private String exchange;
    private String routingKey;

}
