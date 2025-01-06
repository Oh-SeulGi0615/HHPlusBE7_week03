package kr.hhplus.be.server.domain.dto;

public class PointChargeRequest {
    private Long userid;
    private Long point;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }
}
