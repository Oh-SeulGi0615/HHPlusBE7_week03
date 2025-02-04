package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CouponInventoryService {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> issueCouponScript;

    public CouponInventoryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.issueCouponScript = new DefaultRedisScript<>();
        String script =
                "local stockKey = KEYS[1] \n" +
                        "local queueKey = KEYS[2] \n" +
                        "local capacity = tonumber(ARGV[1]) \n" +
                        "local userId = ARGV[2] \n" +
                        "local now = tonumber(ARGV[3]) \n" +
                        "\n" +

                        "-- 재고 키 초기화 (없으면 capacity로 설정)\n" +
                        "if not redis.call('GET', stockKey) then\n" +
                        "    redis.call('SET', stockKey, capacity)\n" +
                        "end\n" +
                        "local stock = tonumber(redis.call('GET', stockKey))\n" +
                        "if stock <= 0 then\n" +
                        "    return -1\n" +
                        "end\n" +
                        "\n" +

                        "-- 재고 감소\n" +
                        "local newStock = redis.call('DECR', stockKey)\n" +
                        "if newStock < 0 then\n" +
                        "    redis.call('INCR', stockKey)\n" +
                        "    return -1\n" +
                        "end\n" +
                        "\n" +

                        "-- 사용자 발급 기록 추가 (큐에 등록)\n" +
                        "redis.call('ZADD', queueKey, now, userId)\n" +
                        "local queueSize = redis.call('ZCARD', queueKey)\n" +
                        "if queueSize > capacity then\n" +
                        "    redis.call('ZREM', queueKey, userId)\n" +
                        "    redis.call('INCR', stockKey)\n" +
                        "    return -2\n" +
                        "end\n" +
                        "return newStock";
        issueCouponScript.setScriptText(script);
        issueCouponScript.setResultType(Long.class);
    }

    public void issueCoupon(Long couponId, Long capacity, Long userId) {
        String stockKey = "coupon:stock:" + couponId;
        String queueKey = "coupon:queue:" + couponId;
        Long now = System.currentTimeMillis();
        List<String> keys = Arrays.asList(stockKey, queueKey);
        List<String> args = Arrays.asList(capacity.toString(), userId.toString(), now.toString());
        Long result = redisTemplate.execute(issueCouponScript, keys, args.toArray(new String[0]));
        if (result < 0) {
            throw new CouponOutOfStockException("쿠폰이 모두 소진되었습니다.");
        }
    }
}
