package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.domain.coupon.dto.UserCouponServiceDto;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.entity.UserEntity;
import kr.hhplus.be.server.exeption.customExceptions.InvalidUserException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;

    public UserCouponService(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    public List<UserCouponServiceDto> checkAllMyCoupon(Long userId) {
        List<UserCouponEntity> myCouponList = userCouponRepository.findAllByUserId(userId);
        return myCouponList.stream().map(userCouponEntity -> new UserCouponServiceDto(
                userCouponEntity.getUserId(),
                userCouponEntity.getCouponId(),
                userCouponEntity.isStatus()
        )).toList();
    }
}
