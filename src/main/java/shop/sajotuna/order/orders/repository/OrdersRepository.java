package shop.sajotuna.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.sajotuna.order.orders.domain.Orders;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Query("select mo.orders from MemberOrder mo where mo.memberId = :memberId")
    List<Orders> findOrdersByMemberId(int memberId);


    Orders getOrdersById(String id);
}
