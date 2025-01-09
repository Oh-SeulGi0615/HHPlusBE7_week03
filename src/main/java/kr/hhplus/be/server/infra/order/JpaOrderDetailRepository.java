package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaOrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    Optional<OrderDetailEntity> findByGoodsId(Long goodsId);
    Optional<OrderDetailEntity> findByOrderDetailId(Long orderDetailId);
    Optional<OrderDetailEntity> findByOrderId(Long orderId);
    List<OrderDetailEntity> findAllByOrderId(Long orderId);
}
