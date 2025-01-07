package kr.hhplus.be.server.repository;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCouponId(Long couponId);
    Optional<CouponEntity> findByCouponName(String couponName);
    Optional<CouponEntity> findByDueDate(LocalDate dueDate);
    List<CouponEntity> findAll();

    @Modifying
    @Transactional
    @Query("UPDATE CouponEntity c SET c.capacity = c.capacity - 1 WHERE c.couponId = :couponId")
    int decrementCapacity(@Param("couponId") Long couponId);
}
