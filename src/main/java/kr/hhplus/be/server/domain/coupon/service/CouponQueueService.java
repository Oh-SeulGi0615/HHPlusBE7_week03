package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.customExceptions.ExistCouponException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CouponQueueService {
    private final StringRedisTemplate redisTemplate;

    public CouponQueueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean addToQueue(Long userId, Long couponId, Long capacity) {
        String queueKey = "coupon:queue:" + couponId;
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(queueKey, userId.toString(), score);
        Long queueSize = redisTemplate.opsForZSet().zCard(queueKey);
        if (queueSize == null || queueSize > capacity) {
            redisTemplate.opsForZSet().remove(queueKey, userId.toString());
            throw new CouponOutOfStockException("선착순 쿠폰 마감되었습니다.");
        }
        return true;
    }

    public boolean checkUserInQueue(Long userId, Long couponId, Long capacity) {
        String queueKey = "coupon:queue" + couponId;
        Long rank = redisTemplate.opsForZSet().rank(queueKey, userId.toString());
        if (rank == null || rank >= capacity) {
            throw new CouponOutOfStockException("선착순 쿠폰 범위를 초과하였습니다.");
        }
        return true;
    }
}
