package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.PaymentStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Optional<PaymentEntity> findByPaymentId(Long paymentId);
    Optional<PaymentEntity> findByOrderId(Long orderId);
    List<PaymentEntity> findAll();
    PaymentEntity save(PaymentEntity paymentEntity);
}
