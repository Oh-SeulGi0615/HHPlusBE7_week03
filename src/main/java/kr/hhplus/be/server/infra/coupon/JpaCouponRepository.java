package kr.hhplus.be.server.infra.coupon;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCouponRepository extends JpaRepository<CouponEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CouponEntity c where c.couponId = :couponId")
    Optional<CouponEntity> findByCouponId(Long couponId);

    Optional<CouponEntity> findByCouponName(String couponName);
    Optional<CouponEntity> findByDueDate(LocalDate dueDate);
    List<CouponEntity> findAll();
}
