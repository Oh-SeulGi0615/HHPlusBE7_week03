package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<OrderEntity> findByUserId(Long userId);
    Optional<OrderEntity> findByOrderId(Long orderId);
    List<OrderEntity> findAll();
    List<OrderEntity> findAllByUserId(Long userId);
    OrderEntity save(OrderEntity orderEntity);
}
