package shop.sajotuna.order.orders.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.order")
@AllArgsConstructor
@Getter
public class OrderRabbitProperties {
    private String queue;
    private String exchange;
    private String routingKey;

    private String dlxQueue;
    private String dlxExchange;
    private String dlxRoutingKey;
}
