package kr.hhplus.be.server.domain.dto.request;

public class UserRequest {
    private String name;

    public UserRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
