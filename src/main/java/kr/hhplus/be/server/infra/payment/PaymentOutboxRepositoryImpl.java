package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.entity.PaymentOutboxEntity;
import kr.hhplus.be.server.domain.payment.repository.PaymentOutboxRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {
    private final JpaPaymentOutboxRepository jpaPaymentOutboxRepository;

    public PaymentOutboxRepositoryImpl(JpaPaymentOutboxRepository jpaPaymentOutboxRepository) {
        this.jpaPaymentOutboxRepository = jpaPaymentOutboxRepository;
    }

    @Override
    public List<PaymentOutboxEntity> findByProcessedFalse() {
        return jpaPaymentOutboxRepository.findByProcessedFalse();
    }

    @Override
    public PaymentOutboxEntity save(PaymentOutboxEntity outboxEvent) {
        return jpaPaymentOutboxRepository.save(outboxEvent);
    }
}
