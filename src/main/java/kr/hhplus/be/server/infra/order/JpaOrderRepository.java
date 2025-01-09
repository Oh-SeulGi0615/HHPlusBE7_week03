package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByUserId(Long userId);
    Optional<OrderEntity> findByOrderId(Long orderId);

    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :status WHERE o.orderId = :orderId")
    OrderEntity updateOrderStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);
}
