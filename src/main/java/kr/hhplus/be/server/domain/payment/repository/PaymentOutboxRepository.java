package kr.hhplus.be.server.domain.payment.repository;

import kr.hhplus.be.server.domain.payment.entity.PaymentOutboxEntity;

import java.util.List;

public interface PaymentOutboxRepository {
    List<PaymentOutboxEntity> findByProcessedFalse();

    PaymentOutboxEntity save(PaymentOutboxEntity outboxEvent);
}
