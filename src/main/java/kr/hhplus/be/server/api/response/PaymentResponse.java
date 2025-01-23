package kr.hhplus.be.server.api.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.enums.PaymentStatus;

public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private Long couponId;
    private Long totalPrice;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public PaymentResponse(){}

    public PaymentResponse(Long paymentId, Long orderId, Long couponId, Long totalPrice, PaymentStatus status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.couponId = couponId;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public PaymentResponse(Long paymentId, Long orderId, Long couponId, Long totalPrice) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.couponId = couponId;
        this.totalPrice = totalPrice;
        this.status = PaymentStatus.WAITING;
    }

    public PaymentResponse(Long paymentId, Long orderId, Long totalPrice) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.couponId = 0L;
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
}
