package shop.sajotuna.order.point.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.point.domain.PointHistory;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> getPointHistoriesByUserId(@NotNull Long userId);
}
