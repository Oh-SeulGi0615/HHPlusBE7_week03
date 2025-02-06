package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    List<UserCouponEntity> findByCouponId(Long couponId);
    List<UserCouponEntity> findAllByUserId(Long userId);
    UserCouponEntity save(UserCouponEntity userCoupon);
    Optional<UserCouponEntity> findByCouponIdAndUserId(Long couponId, Long userId);
}
