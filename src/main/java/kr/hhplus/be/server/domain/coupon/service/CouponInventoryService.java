package kr.hhplus.be.server.domain.coupon.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CouponInventoryService {
    private final StringRedisTemplate redisTemplate;

    public CouponInventoryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean decreaseCouponStock(Long couponId) {
        String couponStockKey = "coupon:stock:" + couponId;
        Long stock = redisTemplate.opsForValue().decrement(couponStockKey);

        if (stock == null || stock < 0) {
            redisTemplate.opsForValue().increment(couponStockKey);
            return false;
        }
        return true;
    }
}
