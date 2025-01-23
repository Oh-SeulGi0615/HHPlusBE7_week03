package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.OrderDetailEntity;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository {
    Optional<OrderDetailEntity> findByGoodsId(Long goodsId);
    Optional<OrderDetailEntity> findByOrderDetailId(Long orderDetailId);
    Optional<OrderDetailEntity> findByOrderId(Long orderId);
    List<OrderDetailEntity> findAllByOrderId(Long orderId);
    OrderDetailEntity save(OrderDetailEntity orderDetailEntity);
}
