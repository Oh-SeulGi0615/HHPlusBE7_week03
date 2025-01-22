package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.entity.OrderEntity;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private JpaOrderRepository jpaOrderRepository;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    @Override
    public Optional<OrderEntity> findByUserId(Long userId) {
        return jpaOrderRepository.findByUserId(userId);
    }

    @Override
    public Optional<OrderEntity> findByOrderId(Long orderId) {
        return jpaOrderRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderEntity> findAll() {
        return jpaOrderRepository.findAll();
    }

    @Override
    public List<OrderEntity> findAllByUserId(Long userId) {
        return jpaOrderRepository.findAllByUserId(userId);
    }

    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        return jpaOrderRepository.save(orderEntity);
    }
}
