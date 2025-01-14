package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.enums.UserCouponStatus;

@Entity
@Table(name = "user_coupon")
public class UserCouponEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long couponId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserCouponStatus status;

    protected UserCouponEntity(){}

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

    public UserCouponStatus isStatus() {
        return status;
    }

    public void setStatus(UserCouponStatus status) {
        this.status = status;
    }
}
