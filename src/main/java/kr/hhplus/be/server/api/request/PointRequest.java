package kr.hhplus.be.server.api.request;

public class PointRequest {
    private Long userid;
    private Long point;

    public PointRequest(){}

    public PointRequest(Long userid, Long point) {
        this.userid = userid;
        this.point = point;
    }

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
