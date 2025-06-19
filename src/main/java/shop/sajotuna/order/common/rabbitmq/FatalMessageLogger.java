package shop.sajotuna.order.common.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FatalMessageLogger {

    private final FatalMessageRepository fatalMessageRepository;

    public void logFatalMessage(Message message, Throwable t) {
        FatalMessageLog log = FatalMessageLog.builder()
                .payload(new String(message.getBody(), StandardCharsets.UTF_8))
                .headers(message.getMessageProperties().getHeaders().toString())
                .exceptionType(t.getCause().getClass().getSimpleName())
                .exceptionMessage(t.getCause().getMessage())
                .occurredAt(LocalDateTime.now())
                .build();

        fatalMessageRepository.save(log);
    }

    public void logParkingLotMessage(Message message) {
        FatalMessageLog log = FatalMessageLog.builder()
                .payload(new String(message.getBody(), StandardCharsets.UTF_8))
                .headers(message.getMessageProperties().getHeaders().toString())
                .exceptionType("RetryableException")
                .exceptionMessage("Retry count exceeded. Moved to parking-lot queue.")
                .occurredAt(LocalDateTime.now())
                .build();

        fatalMessageRepository.save(log);
    }
}
