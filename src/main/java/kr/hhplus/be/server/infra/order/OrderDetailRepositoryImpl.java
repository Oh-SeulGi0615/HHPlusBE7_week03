package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderDetailEntity;
import kr.hhplus.be.server.domain.order.OrderDetailRepository;

import java.util.List;
import java.util.Optional;

public class OrderDetailRepositoryImpl implements OrderDetailRepository {
    private JpaOrderDetailRepository jpaOrderDetailRepository;

    public OrderDetailRepositoryImpl(JpaOrderDetailRepository jpaOrderDetailRepository) {
        this.jpaOrderDetailRepository = jpaOrderDetailRepository;
    }

    @Override
    public Optional<OrderDetailEntity> findByGoodsId(Long goodsId) {
        return jpaOrderDetailRepository.findByGoodsId(goodsId);
    }

    @Override
    public Optional<OrderDetailEntity> findByOrderDetailId(Long orderDetailId) {
        return jpaOrderDetailRepository.findByOrderDetailId(orderDetailId);
    }

    @Override
    public Optional<OrderDetailEntity> findByOrderId(Long orderId) {
        return jpaOrderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetailEntity> findAllByOrderId(Long orderId) {
        return jpaOrderDetailRepository.findAllByOrderId(orderId);
    }

    @Override
    public OrderDetailEntity save(OrderDetailEntity orderDetailEntity) {
        return jpaOrderDetailRepository.save(orderDetailEntity);
    }
}
