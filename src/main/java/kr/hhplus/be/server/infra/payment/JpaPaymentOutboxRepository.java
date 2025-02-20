package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.entity.PaymentOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaPaymentOutboxRepository extends JpaRepository<PaymentOutboxEntity, Long> {
    List<PaymentOutboxEntity> findByProcessedFalse();
}
