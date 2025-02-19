package kr.hhplus.be.server.domain.user.dto;

public class UserServiceDto {
    private Long userId;
    private String userName;
    private Long point;

    public UserServiceDto(Long userId, String userName, Long point) {
        this.userId = userId;
        this.userName = userName;
        this.point = point;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }
}
