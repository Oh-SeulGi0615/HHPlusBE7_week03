package kr.hhplus.be.server.api.request;

public class PaymentRequest {
    private Long userId;
    private Long orderId;
    private Long couponId;

    public PaymentRequest(Long userId, Long orderId, Long couponId) {
        this.userId = userId;
        this.orderId = orderId;
        this.couponId = couponId;
    }

    public PaymentRequest(Long userId, Long orderId) {
        this.userId = userId;
        this.orderId = orderId;
        this.couponId = null;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getCouponId() {
        return couponId;
    }
}
