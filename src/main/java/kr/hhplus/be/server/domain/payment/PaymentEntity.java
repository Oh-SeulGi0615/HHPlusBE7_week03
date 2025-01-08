package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.enums.PaymentStatus;

@Entity
@Table(name = "payment")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payId;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
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

    public Long getPayId() {
        return payId;
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
}
