package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.dto.event.PointEarnRequest;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointEventListenerTest {

    @Mock
    private PointQueueService pointQueueService;

    @InjectMocks
    private PointEventListener pointEventListener;

    @Test
    @DisplayName("포인트 적립 이벤트 처리")
    void handlePointEarnEvent() {
        // given
        PointEarnRequest event = new PointEarnRequest(1L, PointPolicyType.PURCHASE, Money.of(1000));
        doNothing().when(pointQueueService).sendEarnPointsMessage(any(PointEarnRequest.class));
        // when
        pointEventListener.handlePointEarnEvent(event);

        // then
        verify(pointQueueService).sendEarnPointsMessage(any(PointEarnRequest.class));
    }

}