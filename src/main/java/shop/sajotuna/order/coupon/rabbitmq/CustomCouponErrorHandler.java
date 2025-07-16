package shop.sajotuna.order.coupon.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.util.ErrorHandler;

@RequiredArgsConstructor
public class CustomCouponErrorHandler implements ErrorHandler{
    private final FatalExceptionStrategy fatalExceptionStrategy;
    private final CouponFatalMessageLogger couponFatalMessageLogger;

    @Override
    public void handleError(Throwable t) {
        if (fatalExceptionStrategy.isFatal(t) && t instanceof ListenerExecutionFailedException ex) {
            couponFatalMessageLogger.logCouponFatalMessageLog(ex.getFailedMessage(), t);

            throw new ImmediateAcknowledgeAmqpException(
                    "Fatal exception encountered. Retry is futile: " + t.getMessage(), t);
        }

        throw new AmqpRejectAndDontRequeueException(
                "Retryable exception encountered. Moving to DLX for retries: " + t.getMessage(), t);
    }
}
