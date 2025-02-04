package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.coupon.dto.UserCouponServiceDto;
import kr.hhplus.be.server.domain.user.dto.PointServiceDto;
import kr.hhplus.be.server.domain.user.dto.UserServiceDto;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import kr.hhplus.be.server.domain.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFacade {
    private final UserService userService;
    private final UserCouponService userCouponService;

    public UserFacade(UserService userService, UserCouponService userCouponService) {
        this.userService = userService;
        this.userCouponService = userCouponService;
    }

    public UserServiceDto createUser(String userName) {
        return userService.createUser(userName);
    }

    public PointServiceDto chargePoint(Long userId, Long point) {
        return userService.chargePoint(userId, point);
    }

    public PointServiceDto checkPoint(Long userId) {
        return userService.checkPoint(userId);
    }

    public List<UserCouponServiceDto> checkAllMyCoupon(Long userId) {
        userService.checkValidateUser(userId);
        return userCouponService.checkAllMyCoupon(userId);
    }
}
