package shop.sajotuna.order.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.point.domain.Grade;
import shop.sajotuna.order.point.domain.GradePointPolicy;

public interface GradePointPolicyRepository extends JpaRepository<GradePointPolicy, Long> {
    GradePointPolicy findByGrade(Grade grade);
}
