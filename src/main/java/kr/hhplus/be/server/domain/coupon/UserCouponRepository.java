package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCouponEntity> findByCouponId(Long couponId);
    List<UserCouponEntity> findAllByUserId(Long userId);
    UserCouponEntity save(UserCouponEntity userCoupon);
}
