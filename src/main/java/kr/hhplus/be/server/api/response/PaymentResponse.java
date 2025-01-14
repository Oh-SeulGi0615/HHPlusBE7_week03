package kr.hhplus.be.server.api.response;

import kr.hhplus.be.server.enums.PaymentStatus;

public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private Long couponId;
    private Long totalPrice;
    private Enum status;

    public PaymentResponse(){}

    public PaymentResponse(Long paymentId, Long orderId, Long couponId, Long totalPrice, Enum status) {
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
}
