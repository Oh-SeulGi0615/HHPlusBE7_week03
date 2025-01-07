package kr.hhplus.be.server.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_coupon")
public class UserCouponEntity extends BaseEntity{
    @Id
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long couponId;

    @Column(nullable = false)
    private boolean status;

    public UserCouponEntity(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = false;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public boolean isStatus() {
        return status;
    }
}
