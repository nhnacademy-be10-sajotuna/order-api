package shop.sajotuna.order.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.sajotuna.order.point.controller.request.PointEarnRequest;
import shop.sajotuna.order.point.domain.PointHistory;
import shop.sajotuna.order.point.domain.UserPoint;
import shop.sajotuna.order.point.exception.UserPointNotFoundException;
import shop.sajotuna.order.point.repository.PointHistoryRepository;
import shop.sajotuna.order.point.repository.UserPointRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointEarnEventListener {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @RabbitListener(queues = "${rabbitmq.point.queue}")
    @Transactional
    public void earnPoints(PointEarnRequest request) {
        log.info("Received PointEarnRequest: {}", request);
        UserPoint userPoint = userPointRepository.findByUserId(request.getUserId()).orElseThrow(UserPointNotFoundException::new);
        userPoint.earnPoint(request.getPointAmount());
        userPointRepository.save(userPoint);
        pointHistoryRepository.save(PointHistory.createEarnHistory(request.getUserId(), request.getPointAmount()));
    }
}
