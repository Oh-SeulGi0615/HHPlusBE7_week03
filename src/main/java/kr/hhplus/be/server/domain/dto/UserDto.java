package kr.hhplus.be.server.domain.dto;

public class UserDto {
    private Long id;
    private String name;
    private Long point;

    public UserDto(Long id, String name) {
        this.name = name;
        this.id = id;
        this.point = 0L;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPoint() {
        return point;
    }
}