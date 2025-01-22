package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDate;

public class CouponServiceDto {
    private Long couponId;
    private String couponName;
    private Long discountRate;
    private Long capacity;
    private LocalDate dueDate;

    public CouponServiceDto(Long couponId, String couponName, Long discountRate, Long capacity, LocalDate dueDate) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.discountRate = discountRate;
        this.capacity = capacity;
        this.dueDate = dueDate;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Long getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Long discountRate) {
        this.discountRate = discountRate;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
