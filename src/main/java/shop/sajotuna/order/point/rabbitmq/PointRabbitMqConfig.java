package shop.sajotuna.order.point.rabbitmq;

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
public class PointRabbitMqConfig {

    private final PointRabbitProperties pointRabbitProperties;
    private final PointFatalMessageLogger pointFatalMessageLogger;
    private final CustomExceptionStrategy customExceptionStrategy;

    @Bean
    public DirectExchange pointExchange() {
        return new DirectExchange(pointRabbitProperties.getExchange());
    }

    @Bean
    public DirectExchange pointDlx() {
        return new DirectExchange(pointRabbitProperties.getDlxExchange());
    }

    @Bean
    public Queue pointQueue() {
        return QueueBuilder.durable(pointRabbitProperties.getQueue())
                .withArgument("x-dead-letter-exchange", pointRabbitProperties.getDlxExchange())
                .withArgument("x-dead-letter-routing-key", pointRabbitProperties.getDlxRoutingKey())
                .build();
    }

    @Bean
    public Queue pointDlq() {
        return QueueBuilder.durable(pointRabbitProperties.getDlxQueue()).build();
    }

    @Bean
    public Binding pointBinding() {
        return BindingBuilder
                .bind(pointQueue())
                .to(pointExchange())
                .with(pointRabbitProperties.getRoutingKey());
    }

    @Bean
    public Binding pointDlxBinding() {
        return BindingBuilder
                .bind(pointDlq())
                .to(pointDlx())
                .with(pointRabbitProperties.getDlxRoutingKey());
    }

    @Bean
    public DirectExchange pointParkingLotExchange() {
        return new DirectExchange(pointRabbitProperties.getParkingLotExchange());
    }

    @Bean
    public Queue pointParkingLotQueue() {
        return QueueBuilder.durable(pointRabbitProperties.getParkingLotQueue()).build();
    }

    @Bean
    public Binding pointParkingLotBinding() {
        return BindingBuilder
                .bind(pointParkingLotQueue())
                .to(pointParkingLotExchange())
                .with(pointRabbitProperties.getParkingLotRoutingKey());
    }

    @Bean
    public ErrorHandler pointErrorHandler() {
        return new CustomPointErrorHandler(customExceptionStrategy, pointFatalMessageLogger);
    }

    @Bean("pointListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory pointListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        factory.setErrorHandler(pointErrorHandler());

        return factory;
    }
}