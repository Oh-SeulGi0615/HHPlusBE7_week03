package kr.hhplus.be.server.domain.coupon.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.aop.DistributedLock;
import kr.hhplus.be.server.api.request.GetCouponRequest;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.domain.coupon.dto.CouponServiceDto;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.enums.UserCouponStatus;
import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.customExceptions.ExistCouponException;
import kr.hhplus.be.server.exeption.customExceptions.ExpiredCouponException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidCouponException;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {
    private static final Logger log = LoggerFactory.getLogger(CouponService.class);
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

    public CouponServiceDto createCoupon(String couponName, Long discountRate, Long capacity, LocalDate dueDate) {
        if (couponRepository.findByCouponName(couponName).isPresent()) {
            throw new ExistCouponException("이미 등록된 쿠폰입니다.");
        }
        CouponEntity couponEntity = new CouponEntity(
                couponName, discountRate, capacity, dueDate
        );
        CouponEntity savedCoupon = couponRepository.save(couponEntity);
        CouponServiceDto couponServiceDto = new CouponServiceDto(
                savedCoupon.getCouponId(),
                savedCoupon.getCouponName(),
                savedCoupon.getDiscountRate(),
                savedCoupon.getCapacity(),
                savedCoupon.getDueDate()
        );
        return couponServiceDto;
    }

    public List<CouponServiceDto> allCouponList() {
        List<CouponEntity> couponList = couponRepository.findAll();
        return couponList.stream()
                .map(coupon -> new CouponServiceDto(
                        coupon.getCouponId(),
                        coupon.getCouponName(),
                        coupon.getDiscountRate(),
                        coupon.getCapacity(),
                        coupon.getDueDate()
                ))
                .collect(Collectors.toList());
    }

    @DistributedLock(key = "COUPON_ISSUE", waitTime = 5, leaseTime = 10)
    public CouponServiceDto issueCoupon(Long userId, Long couponId) {
        CouponEntity coupon = couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new InvalidCouponException("존재하지 않는 쿠폰입니다."));

        if (userCouponRepository.findByCouponIdAndUserId(couponId, userId).isPresent()) {
            throw new ExistCouponException("이미 발급받은 쿠폰입니다.");
        }

        if (coupon.getCapacity() < 1) {
            throw new CouponOutOfStockException("쿠폰이 모두 소진되었습니다.");
        }
        coupon.setCapacity(coupon.getCapacity() - 1);
        couponRepository.save(coupon);

        UserCouponEntity userCoupon = new UserCouponEntity(userId, couponId);
        userCouponRepository.save(userCoupon);

        return new CouponServiceDto(
                couponId,
                coupon.getCouponName(),
                coupon.getDiscountRate(),
                coupon.getCapacity(),
                coupon.getDueDate());
    }

    @Transactional
    public CouponServiceDto issueCouponPessimistic(Long userId, Long couponId) {
        CouponEntity coupon = couponRepository.findByCouponIdPessimistic(couponId)
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

        return new CouponServiceDto(
                couponId,
                coupon.getCouponName(),
                coupon.getDiscountRate(),
                coupon.getCapacity(),
                coupon.getDueDate());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CouponServiceDto issueCouponOptimistic(Long userId, Long couponId) {
        CouponEntity coupon = couponRepository.findByCouponIdOptimistic(couponId)
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

        return new CouponServiceDto(
                couponId,
                coupon.getCouponName(),
                coupon.getDiscountRate(),
                coupon.getCapacity(),
                coupon.getDueDate());
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
