package shop.sajotuna.order.point.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.sajotuna.order.point.domain.PointHistory;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> getPointHistoriesByUserId(@NotNull Long userId);
    
    Page<PointHistory> getPointHistoriesByUserId(@NotNull Long userId, Pageable pageable);
}
