package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidCouponException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
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

    public boolean enqueueUser(Long couponId, Long userId) {
        String queueKey = "coupon:queue:" + couponId;
        String countKey = "coupon:count:" + couponId;
        String seqKey = "coupon:seq:" + couponId;

        String capacityStr = redisTemplate.opsForValue().get(countKey);
        if (capacityStr == null) {
            setCouponCount(couponId);
        }

        Long currentMillis = System.currentTimeMillis();
        Long seq = redisTemplate.opsForValue().increment(seqKey);
        Long score = currentMillis * 10000 + (seq % 10000);

        String luaScript =
                "local capacity = tonumber(redis.call('GET', KEYS[1])) " +      // 쿠폰 재고 가져오기
                        "local queueLength = tonumber(redis.call('ZCARD', KEYS[2])) " +    // 큐의 현재 길이 가져오기
                        "if not capacity or capacity <= 0 then " +
                        "   return -1 " +                                                 // 재고 없음
                        "end " +
                        "if queueLength >= capacity then " +
                        "   return -1 " +                                                 // 이미 큐의 길이가 재고 이상이면 거부
                        "end " +
                        "redis.call('ZADD', KEYS[2], ARGV[1], ARGV[2]) " +                // 큐에 사용자 추가 (스코어와 사용자 ID)
                        "local rank = redis.call('ZRANK', KEYS[2], ARGV[2]) " +             // 등록 후 해당 사용자의 순위 확인
                        "if rank >= capacity then " +
                        "   redis.call('ZREM', KEYS[2], ARGV[2]) " +                        // 순위 초과면 제거하고 실패
                        "   return -1 " +
                        "end " +
                        "return rank ";

        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Long.class),
                Arrays.asList(countKey, queueKey),
                String.valueOf(score),
                userId.toString()
        );

        if (result == null || result == -1) {
            throw new CouponOutOfStockException("쿠폰이 모두 소진되었습니다.");
        }
        return true;
    }

    public Long dequeueUser(Long couponId) {
        String queueKey = "coupon:queue:" + couponId;
        try {
            Long size = redisTemplate.opsForZSet().size(queueKey);
            if (size == null || size == 0) {
                return null;
            }
            var tuple = redisTemplate.opsForZSet().popMin(queueKey);
            if (tuple == null || tuple.getValue() == null) {
                return null;
            }
            return Long.parseLong(tuple.getValue());
        } catch (Exception e) {
            return null;
        }
    }

    public Long setCouponCount(Long couponId) {
        String couponCountKey = "coupon:count:" + couponId;

        CouponEntity couponEntity = couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new InvalidCouponException("해당 쿠폰이 존재하지 않습니다."));

        Long capacity = couponEntity.getCapacity();
        if (capacity <= 0) {
            throw new CouponOutOfStockException("쿠폰 재고가 없습니다.");
        }

        redisTemplate.opsForValue().set(couponCountKey, capacity.toString(), Duration.ofDays(1));
        return capacity;
    }

    @Scheduled(fixedRate = 1000)
    public void processCouponQueue() {
        Set<String> couponKeys = redisTemplate.keys("coupon:queue:*");
        if (couponKeys != null) {
            for (String queueKey : couponKeys) {
                Long couponId = Long.parseLong(queueKey.split(":")[2]);
                processIssueCoupon(couponId);
            }
        }
    }

    @Transactional
    public void processIssueCoupon(Long couponId) {
        while (true) {
            Long userId = dequeueUser(couponId);
            if (userId == null) {
                break;
            }
            couponService.updateCouponInfo(userId, couponId);
        }
    }
}
