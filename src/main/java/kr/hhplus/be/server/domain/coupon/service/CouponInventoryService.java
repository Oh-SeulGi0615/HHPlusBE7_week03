package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CouponInventoryService {
    private final StringRedisTemplate redisTemplate;

    public CouponInventoryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void decreaseCouponStock(Long couponId) {
        String couponStockKey = "coupon:stock:" + couponId;
        Long stock = redisTemplate.opsForValue().decrement(couponStockKey);

        if (stock < 0) {
            redisTemplate.opsForValue().increment(couponStockKey);
            throw new CouponOutOfStockException("쿠폰이 모두 소진되었습니다.");
        }
    }
}
