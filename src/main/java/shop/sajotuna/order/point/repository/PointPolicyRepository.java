package shop.sajotuna.order.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.point.domain.PointPolicy;
import shop.sajotuna.order.point.domain.PointPolicyType;

import java.util.Optional;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
    Optional<PointPolicy> findByType(PointPolicyType type);
}
