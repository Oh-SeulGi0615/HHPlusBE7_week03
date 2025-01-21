package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.enums.UserCouponStatus;

public class UserCouponDomainDto {
    private Long userId;
    private Long couponId;
    @Enumerated(EnumType.STRING)
    private UserCouponStatus status;

    public UserCouponDomainDto(Long userId, Long couponId, UserCouponStatus status) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public UserCouponStatus getStatus() {
        return status;
    }

    public void setStatus(UserCouponStatus status) {
        this.status = status;
    }
}
