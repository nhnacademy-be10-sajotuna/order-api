package shop.sajotuna.order.common.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.util.ErrorHandler;

@RequiredArgsConstructor
public class CustomErrorHandler implements ErrorHandler {

    private final FatalExceptionStrategy exceptionStrategy;
    private final FatalMessageLogger fatalMessageLogger;

    @Override
    public void handleError(Throwable t) {
        if (exceptionStrategy.isFatal(t) && t instanceof ListenerExecutionFailedException ex) {
            fatalMessageLogger.logFatalMessage(ex.getFailedMessage(), t);

            throw new ImmediateAcknowledgeAmqpException(
                    "Fatal exception encountered. Retry is futile: " + t.getMessage(), t);
        }

        throw new AmqpRejectAndDontRequeueException(
                "Retryable exception encountered. Moving to DLX for retries: " + t.getMessage(), t);
    }
}
