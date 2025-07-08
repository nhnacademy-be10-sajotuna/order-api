package shop.sajotuna.order.point.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.util.ErrorHandler;

@RequiredArgsConstructor
public class CustomPointErrorHandler implements ErrorHandler {

    private final FatalExceptionStrategy exceptionStrategy;
    private final PointFatalMessageLogger fatalMessageLogger;

    @Override
    public void handleError(Throwable t) {
        if (exceptionStrategy.isFatal(t) && t instanceof ListenerExecutionFailedException ex) {
            fatalMessageLogger.logPointFatalMessageLog(ex.getFailedMessage(), t);

            throw new ImmediateAcknowledgeAmqpException(
                    "Fatal exception encountered. Retry is futile: " + t.getMessage(), t);
        }

        throw new AmqpRejectAndDontRequeueException(
                "Retryable exception encountered. Moving to DLX for retries: " + t.getMessage(), t);
    }
}
