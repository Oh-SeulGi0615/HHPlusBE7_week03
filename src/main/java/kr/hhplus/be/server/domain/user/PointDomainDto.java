package kr.hhplus.be.server.domain.user;

public class PointDomainDto {
    private Long userId;
    private Long point;

    public PointDomainDto(Long userId, Long point) {
        this.userId = userId;
        this.point = point;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }
}
