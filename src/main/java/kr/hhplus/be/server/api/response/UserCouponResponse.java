package kr.hhplus.be.server.api.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.enums.UserCouponStatus;

public class UserCouponResponse {
    private Long userId;
    private Long couponId;
    @Enumerated(EnumType.STRING)
    private UserCouponStatus status;

    public UserCouponResponse(){}

    public UserCouponResponse(Long userId, Long couponId, UserCouponStatus status) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
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
}
