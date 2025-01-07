package kr.hhplus.be.server.domain.dto.response;

public class UserResponse {
    private Long userId;
    private String userName;
    private Long point;

    public UserResponse(Long userId, String userName, Long point) {
        this.userId = userId;
        this.userName = userName;
        this.point = point;
    }
}
