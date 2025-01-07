package kr.hhplus.be.server.domain.dto.response;

public class UserCouponResponse {
    private Long userId;
    private Long couponId;
    private boolean status;

    public UserCouponResponse(Long userId, Long couponId, boolean status) {
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

    public boolean isStatus() {
        return status;
    }
}
