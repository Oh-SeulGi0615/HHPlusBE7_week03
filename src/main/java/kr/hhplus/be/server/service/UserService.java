package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.dto.request.UserRequest;
import kr.hhplus.be.server.domain.dto.request.PointRequest;
import kr.hhplus.be.server.domain.dto.response.CouponResponse;
import kr.hhplus.be.server.domain.dto.response.UserCouponResponse;
import kr.hhplus.be.server.domain.dto.response.UserResponse;
import kr.hhplus.be.server.domain.dto.response.PointResponse;
import kr.hhplus.be.server.domain.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.entity.UserEntity;
import kr.hhplus.be.server.exeption.InvalidPointException;
import kr.hhplus.be.server.exeption.InvalidUserException;
import kr.hhplus.be.server.repository.UserCouponRepository;
import kr.hhplus.be.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserCouponRepository userCouponRepository) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.findByUserName(userRequest.getName()).isPresent()) {
            throw new InvalidUserException("이미 등록된 유저입니다.");
        }
        UserEntity userEntity = new UserEntity(userRequest.getName());
        UserEntity savedUser = userRepository.save(userEntity);

        return new UserResponse(savedUser.getUserId(), savedUser.getUserName(), savedUser.getPoint());
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

    public PointResponse checkPoint(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        Long currentPoint = userEntity.getPoint();
        return new PointResponse(userId, currentPoint);
    }

    public List<UserCouponResponse> checkAllMyCoupon(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        List<UserCouponEntity> myCouponList = userCouponRepository.findAllByUserId(userEntity.getUserId());
        return myCouponList.stream().map(userCouponEntity -> new UserCouponResponse(
                userCouponEntity.getUserId(),
                userCouponEntity.getCouponId(),
                userCouponEntity.isStatus()
        )).toList();
    }
}
