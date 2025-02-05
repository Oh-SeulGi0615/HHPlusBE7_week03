package kr.hhplus.be.server.domain.coupon.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final StringRedisTemplate redisTemplate;

    public CouponService(CouponRepository couponRepository, UserCouponRepository userCouponRepository, StringRedisTemplate redisTemplate) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
        this.redisTemplate = redisTemplate;
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

    public CouponEntity checkValidateCoupon(Long couponId) {
        CouponEntity couponEntity = couponRepository.findByCouponId(couponId).orElseThrow(() -> new InvalidCouponException("존재하지 않는 쿠폰입니다."));
        if (couponEntity.getDueDate().isBefore(LocalDate.now())) {
            throw new ExpiredCouponException("만료된 쿠폰입니다.");
        }
        return couponEntity;
    }

    public void checkDuplicateCoupon(Long userId, Long couponId) {
        String userCouponKey = "coupon:user:" + userId + ":" + couponId;
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(userCouponKey, "1", 86400, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(isNew)) {
            throw new ExistCouponException("이미 발급받은 쿠폰입니다.");
        }

        if (userCouponRepository.findByCouponIdAndUserId(couponId, userId).isPresent()) {
            redisTemplate.delete(userCouponKey);
            throw new ExistCouponException("이미 발급받은 쿠폰입니다.");
        }
    }

    @Transactional
    public CouponEntity updateCouponInfo(Long userId, Long couponId) {
        CouponEntity couponEntity = couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new InvalidCouponException("쿠폰이 존재하지 않습니다."));
        couponEntity.setCapacity(couponEntity.getCapacity() - 1);

        UserCouponEntity userCouponEntity = new UserCouponEntity(userId, couponId);
        userCouponRepository.save(userCouponEntity);

        return couponEntity;
    }
}
