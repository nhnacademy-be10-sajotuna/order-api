package shop.sajotuna.order.point.service.dto.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class UserGradeRefreshEvent {
    private final Long userId;
}
