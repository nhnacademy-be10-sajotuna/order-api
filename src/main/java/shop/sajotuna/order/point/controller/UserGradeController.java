package shop.sajotuna.order.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.sajotuna.order.point.controller.response.GradePointPolicyResponse;
import shop.sajotuna.order.point.service.UserGradeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grade")
public class UserGradeController {

    private final UserGradeService userGradeService;

    /**
     * 등급 조회 시 없으면 새로 삽입. 조회 시 등급 업데이트도 동시에 진행
     */
    @GetMapping("/{user-id}")
    public ResponseEntity<GradePointPolicyResponse> getUserGradeAndUpdate(@PathVariable(name = "user-id") Long userId) {
        GradePointPolicyResponse gradePointPolicyResponse = userGradeService.findAndUpdateGrade(userId);
        return ResponseEntity.ok(gradePointPolicyResponse);
    }
}
