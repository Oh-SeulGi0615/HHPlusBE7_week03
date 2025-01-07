package kr.hhplus.be.server.domain.dto.response;

import java.time.LocalDate;

public class CouponResponse {
    private Long couponId;
    private String couponName;
    private Long discountRate;
    private Long capacity;
    private LocalDate dueDate;

    public CouponResponse(Long couponId, String couponName, Long discountRate, Long capacity, LocalDate dueDate) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.discountRate = discountRate;
        this.capacity = capacity;
        this.dueDate = dueDate;
    }

    public Long getCouponId() {
        return couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public Long getDiscountRate() {
        return discountRate;
    }

    public Long getCapacity() {
        return capacity;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
