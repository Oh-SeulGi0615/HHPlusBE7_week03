package kr.hhplus.be.server.api.request;

public class GetCouponRequest {
    private Long userId;
    private Long couponId;

    public GetCouponRequest(){}

    public GetCouponRequest(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCouponId() {
        return couponId;
    }
}
