package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
