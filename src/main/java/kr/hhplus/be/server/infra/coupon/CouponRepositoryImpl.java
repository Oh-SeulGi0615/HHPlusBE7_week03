package kr.hhplus.be.server.infra.coupon;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class CouponRepositoryImpl implements CouponRepository {
    private final JpaCouponRepository jpaCouponRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public CouponRepositoryImpl(JpaCouponRepository jpaCouponRepository) {
        this.jpaCouponRepository = jpaCouponRepository;
    }

    @Override
    public Optional<CouponEntity> findByCouponIdPessimistic(Long couponId) {
        return jpaCouponRepository.findByCouponIdPessimistic(couponId);
    }

    @Override
    public Optional<CouponEntity> findByCouponIdOptimistic(Long couponId) {
        return jpaCouponRepository.findByCouponIdOptimistic(couponId);
    }

    @Override
    public Optional<CouponEntity> findByCouponId(Long couponId) {
        return jpaCouponRepository.findByCouponId(couponId);
    }

    @Override
    public Optional<CouponEntity> findByCouponName(String couponName) {
        return jpaCouponRepository.findByCouponName(couponName);
    }

    @Override
    public Optional<CouponEntity> findByDueDate(LocalDate dueDate) {
        return jpaCouponRepository.findByDueDate(dueDate);
    }

    @Override
    public List<CouponEntity> findAll() {
        return jpaCouponRepository.findAll();
    }

    @Override
    public CouponEntity save(CouponEntity couponEntity) {
        jpaCouponRepository.save(couponEntity);
        return couponEntity;
    }
}
