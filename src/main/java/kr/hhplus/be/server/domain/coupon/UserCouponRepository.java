package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCouponEntity> findByCouponId(Long couponId);
    List<UserCouponEntity> findAllByUserId(Long userId);
    UserCouponEntity save(UserCouponEntity userCoupon);
    Optional<UserCouponEntity> findByCouponIdAndUserId(Long couponId, Long userId);
}
