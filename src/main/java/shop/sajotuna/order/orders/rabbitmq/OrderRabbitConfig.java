package shop.sajotuna.order.orders.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OrderRabbitConfig {

    // 테스트를 위해 30초로 설정
    public static final int ORDER_TTL = 30000;

    private final OrderRabbitProperties orderRabbitProperties;

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(orderRabbitProperties.getExchange());
    }

    @Bean
    public DirectExchange orderDlx() {
        return new DirectExchange(orderRabbitProperties.getDlxExchange());
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(orderRabbitProperties.getQueue())
                .withArgument("x-dead-letter-exchange", orderRabbitProperties.getDlxExchange())
                .withArgument("x-dead-letter-routing-key", orderRabbitProperties.getDlxRoutingKey())
                .withArgument("x-message-ttl", ORDER_TTL) // 1 hour
                .build();
    }

    @Bean
    public Queue orderDlq() {
        return QueueBuilder.durable(orderRabbitProperties.getDlxQueue()).build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(orderExchange())
                .with(orderRabbitProperties.getRoutingKey());
    }

    @Bean
    public Binding orderDlxBinding() {
        return BindingBuilder
                .bind(orderDlq())
                .to(orderDlx())
                .with(orderRabbitProperties.getDlxRoutingKey());
    }

    @Bean("orderListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory orderListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);

        return factory;
    }
}
