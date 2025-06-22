package shop.sajotuna.order.common.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@RequiredArgsConstructor
@Configuration
public class RabbitMqConfig {

    private final RabbitMqProperties rabbitMqProperties;
    private final PointRabbitProperties pointRabbitProperties;
    private final FatalMessageLogger fatalMessageLogger;

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
    public Binding dlxBinding() {
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
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMqProperties.getHost());
        connectionFactory.setPort(rabbitMqProperties.getPort());
        connectionFactory.setUsername(rabbitMqProperties.getUsername());
        connectionFactory.setPassword(rabbitMqProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitMqProperties.getVirtualHost());
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public CustomExceptionStrategy customExceptionStrategy() {
        return new CustomExceptionStrategy();
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new CustomErrorHandler(customExceptionStrategy(), fatalMessageLogger);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        factory.setErrorHandler(errorHandler());

        return factory;
    }
}
