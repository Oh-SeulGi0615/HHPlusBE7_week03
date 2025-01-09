package kr.hhplus.be.server.infra.user;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserCouponRepository extends JpaRepository<UserCouponEntity, Long> {
    Optional<UserCouponEntity> findByCouponId(Long couponId);
    List<UserCouponEntity> findAllByUserId(Long userId);
    Optional<UserCouponEntity> findByCouponIdAndUserId(Long couponId, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserCouponEntity u SET u.status = :status WHERE u.userId = :userId AND u.couponId = :couponId")
    Optional<UserCouponEntity> updateStatus(@Param("status") Enum status,
                                                               @Param("userId") Long userId,
                                                               @Param("couponId") Long couponId);
}
