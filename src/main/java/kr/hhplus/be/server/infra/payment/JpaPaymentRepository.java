package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPaymentId(Long paymentId);
    Optional<PaymentEntity> findByOrderId(Long orderId);
    List<PaymentEntity> findAll();
}
