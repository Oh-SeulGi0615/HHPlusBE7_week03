package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.entity.UserCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCouponEntity> findByCouponId(Long couponId);
    List<UserCouponEntity> findAllByUserId(Long userId);
    UserCouponEntity save(UserCouponEntity userCoupon);
}
