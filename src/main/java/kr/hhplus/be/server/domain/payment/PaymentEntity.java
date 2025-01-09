package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.enums.PaymentStatus;

@Entity
@Table(name = "payment")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Long orderId;

    private Long couponId;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false)
    private Enum status;

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

    public Enum getStatus() {
        return status;
    }

    public void setStatus(Enum status) {
        this.status = status;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}
