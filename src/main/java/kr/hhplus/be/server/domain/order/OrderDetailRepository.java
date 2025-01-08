package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository {
    Optional<OrderDetailEntity> findByGoodsId(Long goodsId);
    Optional<OrderDetailEntity> findByOrderDetailId(Long orderDetailId);
    Optional<OrderDetailEntity> findByOrderId(Long orderId);
    List<OrderDetailEntity> findAllByOrderId(Long orderId);
    OrderDetailEntity save(OrderDetailEntity orderDetailEntity);
}
