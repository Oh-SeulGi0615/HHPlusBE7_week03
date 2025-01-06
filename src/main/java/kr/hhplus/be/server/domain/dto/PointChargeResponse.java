package kr.hhplus.be.server.domain.dto;

public class PointChargeResponse {
    private Long userId;
    private Long point;

    public PointChargeResponse(Long userId, Long point) {
        this.userId = userId;
        this.point = point;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPoint() {
        return point;
    }
}
