package shop.sajotuna.order.point.rabbitmq;

import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.stereotype.Component;
import shop.sajotuna.order.common.exception.ApiException;
import shop.sajotuna.order.point.exception.InvalidPriceException;
import shop.sajotuna.order.point.exception.InvalidUserIdException;
import shop.sajotuna.order.point.exception.NegativePointException;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;

@Component
public class CustomExceptionStrategy implements FatalExceptionStrategy {

    private final FatalExceptionStrategy fatalExceptionStrategy = new ConditionalRejectingErrorHandler.DefaultExceptionStrategy();

    @Override
    public boolean isFatal(Throwable t) {
        Throwable cause = t.getCause();
        return fatalExceptionStrategy.isFatal(t)
                || cause instanceof ApiException;
    }
}
