package shop.sajotuna.order.point.domain;

import lombok.Getter;

@Getter
public enum PointPolicyType {
    PURCHASE("구매 적립"),
    REVIEW("리뷰 작성 적립"),
    REVIEW_WITH_IMAGE("리뷰(이미지 포함) 작성 적립"),
    REGISTER("회원가입"),
    RETURNED("환불금 적립"),
    RETURN_USED_POINT("사용된 포인트 복구");

    private final String description;

    PointPolicyType(String description) {
        this.description = description;
    }
}
