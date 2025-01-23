package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.domain.coupon.dto.UserCouponServiceDto;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.dto.UserServiceDto;
import kr.hhplus.be.server.domain.user.dto.PointServiceDto;
import kr.hhplus.be.server.domain.user.entity.UserEntity;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.exeption.customExceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

    public UserServiceDto createUser(String userName) {
        if (userRepository.findByUserName(userName).isPresent()) {
            throw new ExistUserException("이미 등록된 유저입니다.");
        }
        UserEntity userEntity = new UserEntity(userName);
        UserEntity savedUser = userRepository.save(userEntity);

        return new UserServiceDto(savedUser.getUserId(), savedUser.getUserName(), savedUser.getPoint());
    }

    public List<UserEntity> getAllUser() {
        List<UserEntity> userList = userRepository.findAll();
        return userList;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PointServiceDto chargePoint(Long userId, Long point) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        Long currentPoint = userEntity.getPoint();

        if (point < 0 || point % 10 > 0){
            throw new InvalidChargeUnitException("충전 가능한 금액은 0원 초과 10원 단위입니다.");
        }

        if (point > 1000000) {
            throw new ExceedMaxChargeAmountException("1회 충전 가능한 금액은 최대 1,000,000원 입니다.");
        }

        if (point + currentPoint > 10000000) {
            throw new ExceedMaxBalanceException("보유할 수 있는 최대 금액은 10,000,000원 입니다.");
        }

        Long updatedPoint = currentPoint + point;
        userEntity.setPoint(updatedPoint);
        userRepository.saveAndFlush(userEntity);
        return new PointServiceDto(userId, updatedPoint);
    }

    public PointServiceDto checkPoint(Long userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        Long currentPoint = userEntity.getPoint();
        return new PointServiceDto(userId, currentPoint);
    }

    public List<UserCouponServiceDto> checkAllMyCoupon(Long userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(
                () -> new InvalidUserException("유저를 찾을 수 없습니다.")
        );
        List<UserCouponEntity> myCouponList = userCouponRepository.findAllByUserId(userEntity.getUserId());
        return myCouponList.stream().map(userCouponEntity -> new UserCouponServiceDto(
                userCouponEntity.getUserId(),
                userCouponEntity.getCouponId(),
                userCouponEntity.isStatus()
        )).toList();
    }
}
