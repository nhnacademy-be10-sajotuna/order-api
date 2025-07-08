package shop.sajotuna.order.point.rabbitmq;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PointFatalMessageRepository extends JpaRepository<PointFatalMessageLog, Long> {
}
