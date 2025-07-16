package shop.sajotuna.order.coupon.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@RequiredArgsConstructor
@Configuration
public class CouponRabbitMqConfig {

    private final CouponRabbitProperties couponRabbitProperties;
    private final CouponFatalMessageLogger couponFatalMessageLogger;


    @Bean
    public DirectExchange couponExchange() {
        return new DirectExchange(couponRabbitProperties.getExchange());
    }

    @Bean
    public DirectExchange couponDlx() {
        return new DirectExchange(couponRabbitProperties.getDlxExchange());
    }

    @Bean
    public Queue couponQueue() {
        return QueueBuilder.durable(couponRabbitProperties.getQueue())
                .withArgument("x-dead-letter-exchange", couponRabbitProperties.getDlxExchange())
                .withArgument("x-dead-letter-routing-key", couponRabbitProperties.getDlxRoutingKey())
                .build();
    }

    @Bean
    public Queue couponDlq() {
        return QueueBuilder.durable(couponRabbitProperties.getDlxQueue())
                .build();
    }

    @Bean
    public Binding couponBinding() {
        return BindingBuilder
                .bind(couponQueue())
                .to(couponExchange())
                .with(couponRabbitProperties.getRoutingKey());
    }


    @Bean
    public Binding couponDlxBinding() {
        return BindingBuilder
                .bind(couponDlq())
                .to(couponDlx())
                .with(couponRabbitProperties.getDlxRoutingKey());
    }

    @Bean
    public Queue couponParkingLotQueue() {
        return QueueBuilder.durable(couponRabbitProperties.getParkingLotQueue()).build();
    }

    @Bean
    public DirectExchange couponParkingLotExchange() {
        return new DirectExchange(couponRabbitProperties.getParkingLotExchange());
    }

    @Bean
    public Binding couponParkingLotBinding() {
        return BindingBuilder
                .bind(couponParkingLotQueue())
                .to(couponParkingLotExchange())
                .with(couponRabbitProperties.getParkingLotRoutingKey());
    }

    @Bean
    public CustomCouponExceptionStrategy couponExceptionStrategy() {
        return new CustomCouponExceptionStrategy();
    }

    @Bean
    public ErrorHandler couponRabbitErrorHandler() {
        return new CustomCouponErrorHandler(couponExceptionStrategy(), couponFatalMessageLogger);
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
