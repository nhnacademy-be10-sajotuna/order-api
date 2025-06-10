package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.sajotuna.order.orders.entity.Orders;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Query("select mo.orders from UserOrder mo where mo.userId = :userId")
    List<Orders> findOrdersByMemberId(int userId);

}
