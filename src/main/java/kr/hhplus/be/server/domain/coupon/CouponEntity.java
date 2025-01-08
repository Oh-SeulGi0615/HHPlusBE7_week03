package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "coupon")
public class CouponEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(nullable = false)
    private String couponName;

    @Column(nullable = false)
    private Long discountRate;

    @Column(nullable = false)
    private Long capacity;

    @Column(nullable = false)
    private LocalDate dueDate;

    protected CouponEntity() {}

    public CouponEntity(String couponName, Long discountRate, Long capacity, LocalDate dueDate) {
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

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }
}
