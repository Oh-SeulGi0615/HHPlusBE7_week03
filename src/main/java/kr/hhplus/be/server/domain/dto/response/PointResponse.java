package kr.hhplus.be.server.domain.dto.response;

public class PointResponse {
    private Long userId;
    private Long point;

    public PointResponse(Long userId, Long point) {
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
