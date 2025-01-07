package kr.hhplus.be.server.repository.jpa;

import kr.hhplus.be.server.domain.entity.CouponEntity;
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
