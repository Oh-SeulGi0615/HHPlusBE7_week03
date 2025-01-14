package kr.hhplus.be.server.api.response;

public class UserCouponResponse {
    private Long userId;
    private Long couponId;
    private Enum status;

    public UserCouponResponse(){}

    public UserCouponResponse(Long userId, Long couponId, Enum status) {
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

    public Enum isStatus() {
        return status;
    }
}
