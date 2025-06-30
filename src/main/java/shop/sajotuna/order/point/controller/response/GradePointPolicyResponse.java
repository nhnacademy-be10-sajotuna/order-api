package shop.sajotuna.order.point.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.domain.GradePointPolicy;

@AllArgsConstructor
@Getter
public class GradePointPolicyResponse {

    private Grade grade;
    private int minTotalOrderPrice;
    private int maxTotalOrderPrice;
    private int pointRate;

    public static GradePointPolicyResponse from(GradePointPolicy gradePointPolicy) {
        return new GradePointPolicyResponse(
                gradePointPolicy.getGrade(),
                gradePointPolicy.getMinTotalOrderPrice().getAmount(),
                gradePointPolicy.getMaxTotalOrderPrice().getAmount(),
                gradePointPolicy.getPointRate()
        );
    }
}
