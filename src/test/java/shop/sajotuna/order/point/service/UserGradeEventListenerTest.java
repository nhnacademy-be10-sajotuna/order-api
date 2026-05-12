package shop.sajotuna.order.point.service;

import org.junit.jupiter.api.Test;
import shop.sajotuna.order.point.service.dto.event.UserGradeRefreshEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserGradeEventListenerTest {

    @Test
    void handleUserGradeRefreshEvent_updatesUserGrade() {
        UserGradeService userGradeService = mock(UserGradeService.class);
        UserGradeEventListener listener = new UserGradeEventListener(userGradeService);
        UserGradeRefreshEvent event = new UserGradeRefreshEvent(1L);

        listener.handleUserGradeRefreshEvent(event);

        verify(userGradeService).updateGrade(1L);
    }
}
