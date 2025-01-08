package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByUserId(Long userId);
    Optional<OrderEntity> findByOrderId(Long orderId);
}
