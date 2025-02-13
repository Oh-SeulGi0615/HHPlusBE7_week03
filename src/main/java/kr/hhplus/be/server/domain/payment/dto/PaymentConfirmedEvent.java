package kr.hhplus.be.server.domain.payment.dto;

import org.springframework.context.ApplicationEvent;

import java.util.Objects;

public class PaymentConfirmedEvent {
    private final Long paymentId;
    private final Long orderId;
    private final Long userId;
    private final Long couponId;
    private final Long totalPrice;

    public PaymentConfirmedEvent(Long paymentId, Long orderId, Long userId, Long couponId, Long totalPrice) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.couponId = couponId;
        this.totalPrice = totalPrice;
    }
}
