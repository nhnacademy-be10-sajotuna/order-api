package shop.sajotuna.order.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
    PointPolicy findByType(PointPolicyType type);
}
