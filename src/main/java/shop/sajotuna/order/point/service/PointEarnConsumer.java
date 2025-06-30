package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.common.domain.Money;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.domain.UserGrade;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.exception.UserGradeNotFoundException;
import shop.sajotuna.order.point.repository.UserGradeRepository;
import shop.sajotuna.order.point.service.dto.event.PointEvent;
import shop.sajotuna.order.point.repository.UserPointRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEarnConsumer {

    private final UserPointRepository userPointRepository;
    private final PointHistoryWriter pointHistoryWriter;
    private final PointPolicyService pointPolicyService;
    private final UserGradeRepository userGradeRepository;

    // TODO: 역할 분리 필요
    @RabbitListener(queues = "${rabbitmq.point.queue}")
    @Transactional
    public void onMessage(PointEvent event) {
        log.info("Point Earned Event Received: {}", event);
        // 회원 포인트 정보 조회 및 생성
        UserPoint userPoint = userPointRepository.findByUserId(event.getUserId())
                .orElseGet(() -> userPointRepository.save(UserPoint.create(event.getUserId())));

        // 포인트 계산 및 적립
        Money amount;
        if (event.getType() == PointPolicyType.RETURNED) {
            amount = event.getTotalPrice();
        } else {
            amount = pointPolicyService.getPointPolicy(event.getType()).calculatePoint(event.getTotalPrice());
        }

        UserGrade userGrade = userGradeRepository.findByUserId(event.getUserId()).orElseThrow(UserGradeNotFoundException::new);
        Money gradePoint = userGrade.getGrade().calculatePoint(event.getTotalPrice());

        userPoint.earnPoint(amount.plus(gradePoint));

        // 포인트 이력 저장
        pointHistoryWriter.savePointEarnHistory(event.getUserId(), amount, event.getType().getDescription());
    }
}
