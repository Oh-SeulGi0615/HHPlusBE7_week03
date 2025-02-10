package kr.hhplus.be.server.concurrency;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CouponStockConcurrencyTest extends IntergrationTest {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserCouponRepository userCouponRepository;

    static class UserRequestLog {
        Long userId;
        LocalDateTime requestTime;
        String result;

        UserRequestLog(Long userId, LocalDateTime requestTime, String result) {
            this.userId = userId;
            this.requestTime = requestTime;
            this.result = result;
        }

        @Override
        public String toString() {
            return "UserID: " + userId + ", RequestTime: " + requestTime + ", Result: " + result;
        }
    }

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get - 쿠폰 선착순 발급 요청 테스트")
    void issueCouponConcurrencyTest() throws InterruptedException {
        Long couponCapacity = 7L;
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, couponCapacity, LocalDate.now().plusDays(10)
        );

        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);
        List<UserRequestLog> requestLogs = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < 10L; i++) {
            final Long userId = (long) (i + 1);
            executorService.submit(() -> {
                try {
                    latch.await();
                    LocalDateTime requestTime = LocalDateTime.now();
                    var response =
                            given()
                                    .contentType(ContentType.JSON)
                                    .pathParam("couponId", savedCoupon.getCouponId())
                                    .body(userId)
                                    .when()
                                    .post("/api/coupons/{couponId}/request")
                                    .then()
                                    .log().all()
                                    .extract();
                    int statusCode = response.statusCode();
                    if (statusCode == 200) {
                        requestLogs.add(new UserRequestLog(userId, requestTime, "Success"));
                    } else {
                        String errorMessage = response.body().asString();
                        requestLogs.add(new UserRequestLog(userId, requestTime, "Failed - " + errorMessage));
                    }
                } catch (Exception e) {
                    requestLogs.add(new UserRequestLog(userId, LocalDateTime.now(), "Failed - " + e.getMessage()));
                }
            });
        }
        latch.countDown();

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        requestLogs.sort((log1, log2) -> log1.requestTime.compareTo(log2.requestTime));
        System.out.println("1. 요청을 보낸 시간 기준 유저ID 별 성공여부");
        requestLogs.forEach(System.out::println);

        String queueKey = "coupon:queue:" + savedCoupon.getCouponId();
        List<String> zsetUserIds = null;
        try {
            Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().rangeWithScores(queueKey, 0, -1);
            System.out.println("\n2. 레디스 ZSET에 추가된 순서 기준 유저ID 별 스코어");
            zsetUserIds = new ArrayList<>();
            if (tuples != null && !tuples.isEmpty()) {
                for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                    System.out.println("User: " + tuple.getValue() + ", Score: " + tuple.getScore());
                    zsetUserIds.add(tuple.getValue());
                }
            } else {
                System.out.println("Redis queue is empty.");
            }
        } catch (Exception e) {
            System.err.println("Error while retrieving Redis ZSET for coupon "
                    + savedCoupon.getCouponId() + ": " + e.getMessage());
        }

        Thread.sleep(3000);

        List<UserCouponEntity> issuedCoupons = userCouponRepository.findByCouponId(savedCoupon.getCouponId());
        System.out.println("\n3. userCoupon db 테이블 기준 유저ID 별 발급 시간");
        List<UserCouponEntity> sortedCoupons = issuedCoupons.stream()
                .sorted(Comparator.comparing(UserCouponEntity::getCreatedAt))
                .toList();

        List<String> dbUserIds = new ArrayList<>();
        for (UserCouponEntity coupon : sortedCoupons) {
            System.out.println("UserId: " + coupon.getUserId() + ", CreatedAt: " + coupon.getCreatedAt());
            dbUserIds.add(String.valueOf(coupon.getUserId()));
        }

        System.out.println("\n4. Redis ZSET 순서 vs. DB createdAt 순서 비교");
        if (zsetUserIds.size() != dbUserIds.size()) {
            System.out.println(" - 크기가 다릅니다! ZSET=" + zsetUserIds.size() + ", DB=" + dbUserIds.size());
        } else {
            for (int i = 0; i < zsetUserIds.size(); i++) {
                String zsetId = zsetUserIds.get(i);
                String dbId = dbUserIds.get(i);

                // 출력
                System.out.printf(" - [%d] ZSET=%s / DB=%s", i, zsetId, dbId);
                if (zsetId.equals(dbId)) {
                    System.out.println(" (일치)");
                } else {
                    System.out.println(" (불일치)");
                }
            }
        }
        assertEquals(couponCapacity.intValue(), issuedCoupons.size(), "발급된 쿠폰 수가 쿠폰 수량과 일치해야 함 ");
    }
}
