package shop.sajotuna.order.coupon.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rabbitmq.coupon")
@AllArgsConstructor
@Getter
@Setter
public class CouponRabbitProperties {
    private String queue;
    private String exchange;
    private String routingKey;

    private String dlxQueue;
    private String dlxExchange;
    private String dlxRoutingKey;

    private String parkingLotQueue;
    private String parkingLotExchange;
    private String parkingLotRoutingKey;

}
