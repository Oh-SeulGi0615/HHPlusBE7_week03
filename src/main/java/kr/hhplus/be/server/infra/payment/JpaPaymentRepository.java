package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPayId(Long payId);
    Optional<PaymentEntity> findByOrderId(Long orderId);
    List<PaymentEntity> findAll();
    PaymentEntity save(PaymentEntity paymentEntity);
}
