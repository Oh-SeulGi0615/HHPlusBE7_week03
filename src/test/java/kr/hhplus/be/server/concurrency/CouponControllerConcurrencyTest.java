package kr.hhplus.be.server.concurrency;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.request.GetCouponRequest;
import kr.hhplus.be.server.api.response.CouponResponse;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.user.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CouponControllerConcurrencyTest extends IntergrationTest {
    @Autowired
    private RedissonClient redissonClient;

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get - 동시 요청 처리 테스트")
    void getCouponConcurrencyTest() {
        // given
        Long couponCapacity = 8L;
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, couponCapacity, LocalDate.now().plusDays(10)
        );

        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);

        List<Long> successUserIds = new ArrayList<>();
        List<Long> failedUserIds = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Future<?>[] futures = new Future[10];
        for (int i = 0; i < 10L; i++) {
            final Long userId = (long) (i + 1);
            futures[i] = executorService.submit(() -> {
                try {
                    var response =
                            given()
                                    .contentType(ContentType.JSON)
                                    .pathParam("couponId", savedCoupon.getCouponId())
                                    .body(userId)
                                    .when()
                                    .post("/api/coupons/{couponId}/get")
                                    .then()
                                    .log().all()
                                    .extract();
                    int statusCode = response.statusCode();
                    if (statusCode == 200) {
                        CouponResponse couponResp = response.as(CouponResponse.class);

                        synchronized (successUserIds) {
                            successUserIds.add(userId);
                        }
                    } else if (statusCode == 400) {
                        synchronized (failedUserIds) {
                            failedUserIds.add(userId);
                        }
                    } else {
                        synchronized (failedUserIds) {
                            failedUserIds.add(userId);
                        }
                    }
                } catch (Exception e) {
                    synchronized (failedUserIds) {
                        failedUserIds.add(userId);
                    }
                }
            });
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("요청 실패: " + e.getMessage());
            }
        }

        executorService.shutdown();

        long finalCapacity = jpaCouponRepository.findById(savedCoupon.getCouponId())
                .map(CouponEntity::getCapacity)
                .orElseThrow()
                .longValue();

        assertEquals(0, finalCapacity);
        assertEquals(couponCapacity, successUserIds.size());
        assertEquals(10 - couponCapacity, failedUserIds.size());
    }
}
