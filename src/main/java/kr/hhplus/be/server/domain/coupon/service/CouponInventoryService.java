package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidCouponException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class CouponInventoryService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponService couponService;
    private final StringRedisTemplate redisTemplate;

    public CouponInventoryService(CouponRepository couponRepository, UserCouponRepository userCouponRepository, CouponService couponService, StringRedisTemplate redisTemplate) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
        this.couponService = couponService;
        this.redisTemplate = redisTemplate;
    }

    public void enqueueUser(Long couponId, Long userId) {
        String queueKey = "coupon:queue:" + couponId;
        Long timestamp = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(queueKey, userId.toString(), timestamp);
    }

    public Long dequeueUser(Long couponId) {
        String queueKey = "coupon:queue:" + couponId;
        var tuple = redisTemplate.opsForZSet().popMin(queueKey);
        if (tuple == null || tuple.getValue() == null) {
            return null;
        }
        return Long.parseLong(tuple.getValue());
    }

    public void setCouponCount(Long couponId, Long capacity) {
        String couponCountKey = "coupon:count:" + couponId;
        redisTemplate.opsForValue().set(couponCountKey, capacity.toString());
    }

    @Scheduled(fixedRate = 1000)
    public void processCouponQueue(Long couponId) {
        
    }
}
