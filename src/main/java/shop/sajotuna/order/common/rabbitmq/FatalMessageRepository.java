package shop.sajotuna.order.common.rabbitmq;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FatalMessageRepository extends JpaRepository<FatalMessageLog, Long> {
}
