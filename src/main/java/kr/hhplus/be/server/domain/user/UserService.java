package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.api.request.UserRequest;
import kr.hhplus.be.server.api.request.PointRequest;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.api.response.UserResponse;
import kr.hhplus.be.server.api.response.PointResponse;
import kr.hhplus.be.server.domain.coupon.UserCouponDomainDto;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.exeption.customExceptions.ExistUserException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidPointException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidUserException;
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

    public UserDomainDto createUser(String userName) {
        if (userRepository.findByUserName(userName).isPresent()) {
            throw new ExistUserException("이미 등록된 유저입니다.");
        }
        UserEntity userEntity = new UserEntity(userName);
        UserEntity savedUser = userRepository.save(userEntity);

        return new UserDomainDto(savedUser.getUserId(), savedUser.getUserName(), savedUser.getPoint());
    }

    public List<UserEntity> getAllUser() {
        List<UserEntity> userList = userRepository.findAll();
        return userList;
    }

    public PointDomainDto chargePoint(Long userId, Long point) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        Long currentPoint = userEntity.getPoint();

        if (point < 0 || point % 10 > 0){
            throw new InvalidPointException("충전 가능한 금액은 0원 초과 10원 단위입니다.");
        }

        if (point > 1000000) {
            throw new InvalidPointException("1회 충전 가능한 금액은 최대 1,000,000원 입니다.");
        }

        if (point + currentPoint > 10000000) {
            throw new InvalidPointException("보유할 수 있는 최대 금액은 10,000,000원 입니다.");
        }

        Long updatedPoint = currentPoint + point;
        userEntity.setPoint(updatedPoint);
        userRepository.save(userEntity);
        return new PointDomainDto(userId, updatedPoint);
    }

    public PointDomainDto checkPoint(Long userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        Long currentPoint = userEntity.getPoint();
        return new PointDomainDto(userId, currentPoint);
    }

    public List<UserCouponDomainDto> checkAllMyCoupon(Long userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        List<UserCouponEntity> myCouponList = userCouponRepository.findAllByUserId(userEntity.getUserId());
        return myCouponList.stream().map(userCouponEntity -> new UserCouponDomainDto(
                userCouponEntity.getUserId(),
                userCouponEntity.getCouponId(),
                userCouponEntity.isStatus()
        )).toList();
    }
}
