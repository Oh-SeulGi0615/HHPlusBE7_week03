package kr.hhplus.be.server.domain.payment.repository;

import kr.hhplus.be.server.domain.payment.entity.PaymentEntity;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Optional<PaymentEntity> findByPaymentId(Long paymentId);
    Optional<PaymentEntity> findByOrderId(Long orderId);
    List<PaymentEntity> findAll();
    PaymentEntity save(PaymentEntity paymentEntity);
}
