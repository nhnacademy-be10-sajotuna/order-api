package shop.sajotuna.order.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.point.domain.UserPoint;

import java.util.Optional;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {
    Optional<UserPoint> findByUserId(Long userId);
}
