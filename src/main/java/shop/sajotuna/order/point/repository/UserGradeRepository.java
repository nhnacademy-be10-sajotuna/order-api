package shop.sajotuna.order.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.point.domain.UserGrade;

import java.util.Optional;

public interface UserGradeRepository extends JpaRepository<UserGrade, Long> {
    Optional<UserGrade> findByUserId(Long userId);
}
