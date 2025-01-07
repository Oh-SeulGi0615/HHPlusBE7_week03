package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.dto.request.UserRequest;
import kr.hhplus.be.server.domain.dto.request.PointRequest;
import kr.hhplus.be.server.domain.dto.response.UserResponse;
import kr.hhplus.be.server.domain.dto.response.PointResponse;
import kr.hhplus.be.server.domain.entity.UserEntity;
import kr.hhplus.be.server.exeption.InvalidPointException;
import kr.hhplus.be.server.exeption.InvalidUserException;
import kr.hhplus.be.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.findByUserName(userRequest.getName()).isPresent()) {
            throw new InvalidUserException("이미 등록된 유저입니다.");
        }
        UserEntity userEntity = new UserEntity(userRequest.getName());
        userRepository.save(userEntity);

        Optional<UserEntity> userInfo = userRepository.findByUserName(userRequest.getName());
        return new UserResponse(userInfo.get().getUserId(), userInfo.get().getUserName(), userInfo.get().getPoint());
    }

    public List<UserEntity> getAllUser() {
        List<UserEntity> userList = userRepository.findAll();
        return userList;
    }

    public PointResponse chargePoint(PointRequest pointRequest) {
        UserEntity userEntity = userRepository.findById(pointRequest.getUserid()).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        Long currentPoint = userEntity.getPoint();

        if (pointRequest.getPoint() < 0 || pointRequest.getPoint() % 10 > 0){
            throw new InvalidPointException("충전 가능한 금액은 0원 초과 10원 단위입니다.");
        }

        if (pointRequest.getPoint() > 1000000) {
            throw new InvalidPointException("1회 충전 가능한 금액은 최대 1,000,000원 입니다.");
        }

        if (pointRequest.getPoint() + currentPoint > 10000000) {
            throw new InvalidPointException("보유할 수 있는 최대 금액은 10,000,000원 입니다.");
        }

        Long updatedPoint = currentPoint + pointRequest.getPoint();
        userEntity.setPoint(updatedPoint);
        userRepository.save(userEntity);
        return new PointResponse(pointRequest.getUserid(), updatedPoint);
    }

    public PointResponse checkPoint(PointRequest pointRequest) {
        UserEntity userEntity = userRepository.findById(pointRequest.getUserid()).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        Long currentPoint = userEntity.getPoint();
        return new PointResponse(pointRequest.getUserid(), currentPoint);
    }
}
