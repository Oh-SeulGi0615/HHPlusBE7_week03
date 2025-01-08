package kr.hhplus.be.server.infra.user;

import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaUserCouponRepository extends JpaRepository<UserCouponEntity, Long> {
    Optional<UserCouponEntity> findByCouponId(Long couponId);
    List<UserCouponEntity> findAllByUserId(Long userId);
}
