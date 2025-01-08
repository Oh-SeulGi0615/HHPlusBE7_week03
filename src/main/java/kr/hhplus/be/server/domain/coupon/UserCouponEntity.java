package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.enums.UserCouponStatus;

@Entity
@Table(name = "user_coupon")
public class UserCouponEntity extends BaseEntity {
    @Id
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long couponId;

    @Column(nullable = false)
    private Enum status;

    public UserCouponEntity(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = UserCouponStatus.AVAILABLE;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public Enum isStatus() {
        return status;
    }
}
