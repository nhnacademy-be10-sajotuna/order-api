package shop.sajotuna.order.coupon.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class CouponRabbitMqConfig {

    private final CouponRabbitProperties couponRabbitProperties;

    @Bean
    public DirectExchange couponExchange() {
        return new DirectExchange(couponRabbitProperties.getExchange());
    }

    @Bean
    public Queue couponQueue() {
        return QueueBuilder.durable(couponRabbitProperties.getQueue()).build();
    }

    @Bean
    public Binding couponBinding() {
        return BindingBuilder
                .bind(couponQueue())
                .to(couponExchange())
                .with(couponRabbitProperties.getRoutingKey());
    }

    @Bean("couponListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory couponListenerContainerFactory(
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
