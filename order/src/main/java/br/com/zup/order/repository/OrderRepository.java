package br.com.zup.order.repository;

import br.com.zup.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("UPDATE orders o SET status = ?2 where id = ?1")
    @Modifying
    int updateStatus(String orderId, String status);
}
