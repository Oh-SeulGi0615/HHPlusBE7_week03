package kr.hhplus.be.server.infra.user;

import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final JpaUserCouponRepository jpaUserCouponRepository;

    public UserCouponRepositoryImpl(JpaUserCouponRepository jpaUserCouponRepository) {
        this.jpaUserCouponRepository = jpaUserCouponRepository;
    }


    @Override
    public Optional<UserCouponEntity> findByCouponId(Long couponId) {
        return jpaUserCouponRepository.findByCouponId(couponId);
    }

    @Override
    public List<UserCouponEntity> findAllByUserId(Long userId) {
        return jpaUserCouponRepository.findAllByUserId(userId);
    }

    @Override
    public UserCouponEntity save(UserCouponEntity userCoupon) {
        jpaUserCouponRepository.save(userCoupon);
        return userCoupon;
    }

    @Override
    public Optional<UserCouponEntity> findByCouponIdAndUserId(Long couponId, Long userId) {
        return jpaUserCouponRepository.findByCouponIdAndUserId(couponId, userId);
    }
}
