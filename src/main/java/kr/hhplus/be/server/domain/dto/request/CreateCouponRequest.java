package kr.hhplus.be.server.domain.dto.request;

import java.time.LocalDate;

public class CreateCouponRequest {
    private String couponName;
    private Long discountRate;
    private Long capacity;
    private LocalDate dueDate;

    public CreateCouponRequest(String couponName, Long discountRate, Long capacity, LocalDate dueDate) {
        this.couponName = couponName;
        this.discountRate = discountRate;
        this.capacity = capacity;
        this.dueDate = dueDate;
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
