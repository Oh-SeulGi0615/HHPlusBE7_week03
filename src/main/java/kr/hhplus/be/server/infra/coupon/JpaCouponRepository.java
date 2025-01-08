package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JpaCouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCouponId(Long couponId);
    Optional<CouponEntity> findByCouponName(String couponName);
    Optional<CouponEntity> findByDueDate(LocalDate dueDate);
    List<CouponEntity> findAll();
}
