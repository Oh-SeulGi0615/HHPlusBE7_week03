package kr.hhplus.be.server.infra.user;

import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
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
}
