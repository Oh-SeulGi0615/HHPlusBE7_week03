package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.enums.OrderStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<OrderEntity> findByUserId(Long userId);
    Optional<OrderEntity> findByOrderId(Long orderId);
    List<OrderEntity> findAll();
    List<OrderEntity> findAllByUserId(Long userId);
    OrderEntity save(OrderEntity orderEntity);
}
