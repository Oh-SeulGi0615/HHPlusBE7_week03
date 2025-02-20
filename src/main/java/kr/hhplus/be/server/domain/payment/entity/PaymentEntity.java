package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.enums.PaymentStatus;

@Entity
@Table(name = "payment")
public class PaymentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Long orderId;

    private Long couponId;

    @Column(nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    protected PaymentEntity(){}

    public PaymentEntity(Long orderId, Long couponId, Long totalPrice) {
        this.orderId = orderId;
        this.couponId = couponId;
        this.totalPrice = totalPrice;
        this.status = PaymentStatus.WAITING;
    }

    public PaymentEntity(Long orderId, Long totalPrice) {
        this.orderId = orderId;
        this.couponId = null;
        this.totalPrice = totalPrice;
        this.status = PaymentStatus.WAITING;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}
