package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private final JpaPaymentRepository jpaPaymentRepository;

    public PaymentRepositoryImpl(JpaPaymentRepository jpaPaymentRepository) {
        this.jpaPaymentRepository = jpaPaymentRepository;
    }

    @Override
    public Optional<PaymentEntity> findByPayId(Long payId) {
        return jpaPaymentRepository.findByPayId(payId);
    }

    @Override
    public Optional<PaymentEntity> findByOrderId(Long orderId) {
        return jpaPaymentRepository.findByOrderId(orderId);
    }

    @Override
    public List<PaymentEntity> findAll() {
        return jpaPaymentRepository.findAll();
    }

    @Override
    public PaymentEntity save(PaymentEntity paymentEntity) {
        return jpaPaymentRepository.save(paymentEntity);
    }
}
