package kr.hhplus.be.server.api.response;

public class CouponStatusResponse {
    private Long userId;
    private Long couponId;
    private boolean issued;

    public CouponStatusResponse(Long userId, Long couponId, boolean issued) {
        this.userId = userId;
        this.couponId = couponId;
        this.issued = issued;
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

    public boolean isIssued() {
        return issued;
    }

    public void setIssued(boolean issued) {
        this.issued = issued;
    }
}
