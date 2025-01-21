package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.api.request.CreateCouponRequest;
import kr.hhplus.be.server.api.request.GetCouponRequest;
import kr.hhplus.be.server.api.response.CouponResponse;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.enums.UserCouponStatus;
import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.customExceptions.ExistCouponException;
import kr.hhplus.be.server.exeption.customExceptions.ExpiredCouponException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidCouponException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final RedissonClient redissonClient;

    @PersistenceContext
    private EntityManager entityManager;

    public CouponService(CouponRepository couponRepository, UserCouponRepository userCouponRepository, RedissonClient redissonClient) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
        this.redissonClient = redissonClient;
    }

    public CouponResponse createCoupon(CreateCouponRequest createCouponRequest) {
        if (couponRepository.findByCouponName(createCouponRequest.getCouponName()).isPresent()) {
            throw new ExistCouponException("이미 등록된 쿠폰입니다.");
        }
        CouponEntity couponEntity = new CouponEntity(
                createCouponRequest.getCouponName(),
                createCouponRequest.getDiscountRate(),
                createCouponRequest.getCapacity(),
                createCouponRequest.getDueDate()
        );
        CouponEntity savedCoupon = couponRepository.save(couponEntity);
        return new CouponResponse(
                savedCoupon.getCouponId(),
                createCouponRequest.getCouponName(),
                createCouponRequest.getDiscountRate(),
                createCouponRequest.getCapacity(),
                createCouponRequest.getDueDate()
        );
    }

    public List<CouponEntity> allCouponList() {
        List<CouponEntity> couponList = couponRepository.findAll();
        return couponList;
    }

    @Transactional
    public CouponResponse getCoupon(Long userId, Long couponId) {
        CouponEntity coupon = couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new InvalidCouponException("존재하지 않는 쿠폰입니다."));

        if (userCouponRepository.findByCouponIdAndUserId(couponId, userId).isPresent()) {
            throw new ExistCouponException("이미 발급받은 쿠폰입니다.");
        }

        if (coupon.getCapacity() < 1) {
            throw new CouponOutOfStockException("쿠폰이 모두 소진되었습니다.");
        }
        coupon.setCapacity(coupon.getCapacity() - 1);

        UserCouponEntity userCoupon = new UserCouponEntity(userId, couponId);
        userCouponRepository.save(userCoupon);

        return new CouponResponse(
                couponId,
                coupon.getCouponName(),
                coupon.getDiscountRate(),
                coupon.getCapacity(),
                coupon.getDueDate());
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional
    public CouponResponse getCouponWithRedis(Long userId, Long couponId) {
        String lockKey = "coupon_lock_" + couponId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                CouponEntity coupon = couponRepository.findByCouponId(couponId)
                        .orElseThrow(() -> new InvalidCouponException("존재하지 않는 쿠폰입니다."));

                if (userCouponRepository.findByCouponIdAndUserId(couponId, userId).isPresent()) {
                    throw new ExistCouponException("이미 발급받은 쿠폰입니다.");
                }

                if (coupon.getCapacity() < 1) {
                    throw new CouponOutOfStockException("쿠폰이 모두 소진되었습니다.");
                }
                coupon.setCapacity(coupon.getCapacity() - 1);
//                entityManager.flush();
//                entityManager.clear();

                UserCouponEntity userCoupon = new UserCouponEntity(userId, couponId);
                userCouponRepository.save(userCoupon);

                return new CouponResponse(
                        couponId,
                        coupon.getCouponName(),
                        coupon.getDiscountRate(),
                        coupon.getCapacity(),
                        coupon.getDueDate()
                );
            } else {
                throw new RuntimeException("잠시 후 다시 시도해주세요.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public UserCouponResponse useCoupon(GetCouponRequest getCouponRequest) {
       UserCouponEntity myCoupon = userCouponRepository.findByCouponIdAndUserId(
                getCouponRequest.getCouponId(), getCouponRequest.getUserId()
        ).orElseThrow(() -> new InvalidCouponException("존재하지 않는 쿠폰입니다."));

       if (couponRepository.findByCouponId(myCoupon.getCouponId()).get().getDueDate().isBefore(LocalDate.now())) {
           myCoupon.setStatus(UserCouponStatus.EXPIRED);
           throw new ExpiredCouponException("만료된 쿠폰입니다.");
       }

        myCoupon.setStatus(UserCouponStatus.USED);
        return new UserCouponResponse(myCoupon.getUserId(), myCoupon.getCouponId(), myCoupon.isStatus());
    }
}
