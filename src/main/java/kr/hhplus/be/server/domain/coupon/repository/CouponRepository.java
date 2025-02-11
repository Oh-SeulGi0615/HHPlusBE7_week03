package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Optional<CouponEntity> findByCouponId(Long couponId);
    Optional<CouponEntity> findByCouponName(String couponName);
    Optional<CouponEntity> findByDueDate(LocalDate dueDate);
    List<CouponEntity> findAll();
    CouponEntity save(CouponEntity couponEntity);
}
