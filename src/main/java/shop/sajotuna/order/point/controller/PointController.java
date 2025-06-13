package shop.sajotuna.order.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.point.controller.response.PointHistoryResponse;
import shop.sajotuna.order.point.domain.PointPolicyType;
import shop.sajotuna.order.point.service.PointService;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping
    public ResponseEntity<List<PointHistoryResponse>> getPointsByUserId(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(pointService.getPointsByUserId(userId));
    }

    /**
     * 리뷰 작성, 회원가입 시 호출하는 API
     * 호출 시 RequestParam으로 PointPolicyType을 전달받아 해당 정책에 따라 포인트를 적립합니다.
     */
    @PostMapping
    public ResponseEntity<PointHistoryResponse> getPointsByType(@RequestHeader("X-User-Id") Long userId, @RequestParam PointPolicyType type) {
        return ResponseEntity.ok(pointService.earnPointsByType(userId, type));
    }
}
