package shop.sajotuna.order.point.service;

public interface PointPolicyService {
    int getPurchasePoint(int totalPrice);

    int getReviewPoint();

    int getRegisterPoint();
}
