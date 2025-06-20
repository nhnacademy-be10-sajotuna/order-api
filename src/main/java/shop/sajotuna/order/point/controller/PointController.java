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

    @PostMapping
    public ResponseEntity<Void> earnPoints(@RequestHeader("X-User-Id") Long userId,
                                           @RequestParam("type") PointPolicyType type) {
        pointService.earnPointsByType(userId, type);
        return ResponseEntity.ok().build();
    }
}
