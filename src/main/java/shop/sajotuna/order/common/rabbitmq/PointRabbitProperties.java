package shop.sajotuna.order.common.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.point")
@AllArgsConstructor
@Getter
public class PointRabbitProperties {
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
