package kr.hhplus.be.server.api.response;

public class PointResponse {
    private Long userId;
    private Long point;

    public PointResponse(){}

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
