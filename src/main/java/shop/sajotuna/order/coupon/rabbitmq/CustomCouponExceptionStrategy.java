package shop.sajotuna.order.coupon.rabbitmq;

import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import shop.sajotuna.order.coupon.exception.CouponNotFoundException;
import shop.sajotuna.order.point.exception.InvalidPriceException;
import shop.sajotuna.order.point.exception.InvalidUserIdException;
import shop.sajotuna.order.point.exception.NegativePointException;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;

public class CustomCouponExceptionStrategy implements FatalExceptionStrategy {
    private final FatalExceptionStrategy fatalExceptionStrategy = new ConditionalRejectingErrorHandler.DefaultExceptionStrategy();

    @Override
    public boolean isFatal(Throwable t) {
        Throwable cause = t.getCause();
        return fatalExceptionStrategy.isFatal(t)
                || cause instanceof CouponNotFoundException;
    }
}
