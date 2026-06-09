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

    @GetMapping("/{user-id}")
    public ResponseEntity<GradePointPolicyResponse> getUserGrade(@PathVariable(name = "user-id") Long userId) {
        GradePointPolicyResponse gradePointPolicyResponse = userGradeService.getUserGrade(userId);
        return ResponseEntity.ok(gradePointPolicyResponse);
    }
}
