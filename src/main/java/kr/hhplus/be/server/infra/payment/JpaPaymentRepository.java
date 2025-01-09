package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPaymentId(Long paymentId);
    Optional<PaymentEntity> findByOrderId(Long orderId);
    List<PaymentEntity> findAll();
    PaymentEntity save(PaymentEntity paymentEntity);

    @Modifying
    @Query("UPDATE PaymentEntity p SET p.status = :status WHERE p.paymentId = :paymentId")
    PaymentEntity updatePaymentStatus(@Param("paymenId") Long paymentId, @Param("status") PaymentStatus status);
}
