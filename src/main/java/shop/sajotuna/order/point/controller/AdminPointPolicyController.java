package shop.sajotuna.order.point.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.sajotuna.order.point.controller.request.PointPolicyUpdateRequest;
import shop.sajotuna.order.point.controller.response.PointPolicyResponse;
import shop.sajotuna.order.point.service.PointPolicyService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/point-polies")
@RequiredArgsConstructor
public class AdminPointPolicyController {

    private final PointPolicyService pointPolicyService;

    @GetMapping
    public ResponseEntity<List<PointPolicyResponse>> getAllPointPolicies() {
        List<PointPolicyResponse> policies = pointPolicyService.getAllPointPolicies();
        return ResponseEntity.ok(policies);
    }

    @PutMapping("/{policy-id}")
    public ResponseEntity<Void> updatePointPolicy(
            @PathVariable("policy-id") Long policyId,
            @RequestBody @Valid PointPolicyUpdateRequest pointPolicyUpdateRequest) {
        pointPolicyService.updatePointPolicy(policyId, pointPolicyUpdateRequest);
        return ResponseEntity.noContent().build();
    }
}
