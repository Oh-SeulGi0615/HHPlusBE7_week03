package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.enums.PaymentStatus;

public class PaymentServiceDto {
    private Long paymentId;
    private Long orderId;
    private Long couponId;
    private Long totalPrice;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public PaymentServiceDto(Long paymentId, Long orderId, Long couponId, Long totalPrice, PaymentStatus status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.couponId = couponId;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public PaymentServiceDto(Long paymentId, Long orderId, Long couponId, Long totalPrice) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.couponId = couponId;
        this.totalPrice = totalPrice;
        this.status = PaymentStatus.WAITING;
    }

    public PaymentServiceDto(Long paymentId, Long orderId, Long totalPrice) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.couponId = 0L;
        this.totalPrice = totalPrice;
        this.status = PaymentStatus.WAITING;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
