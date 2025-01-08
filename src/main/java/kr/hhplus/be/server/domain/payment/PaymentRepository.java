package kr.hhplus.be.server.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Optional<PaymentEntity> findByPayId(Long payId);
    Optional<PaymentEntity> findByOrderId(Long orderId);
    List<PaymentEntity> findAll();
    PaymentEntity save(PaymentEntity paymentEntity);
}
