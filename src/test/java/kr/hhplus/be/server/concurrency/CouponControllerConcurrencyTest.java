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

@ActiveProfiles("test")
public class CouponControllerConcurrencyTest extends IntergrationTest {
    @Autowired
    private RedissonClient redissonClient;

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get - 동시 요청 처리 테스트")
    void getCouponConcurrencyTest() {
        // given
        Long conponCapacity = 3L;
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, conponCapacity, LocalDate.now().plusDays(10)
        );

        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);

        List<Long> successUserIds = new ArrayList<>();
        List<Long> failedUserIds = new ArrayList<>();
        List<Long> remainCouponsQuantity = new ArrayList<>();

        // 동시 요청을 처리하기 위해 Thread Pool 생성
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 10명의 사용자 요청을 실행
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
                        // 성공 처리
                        CouponResponse couponResp = response.as(CouponResponse.class);

                        synchronized (successUserIds) {
                            successUserIds.add(userId);
                            remainCouponsQuantity.add(couponResp.getCapacity());
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

        // 모든 요청 완료 대기
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("요청 실패: " + e.getMessage());
            }
        }

        executorService.shutdown();

        System.out.println("성공한 사용자 ID: " + successUserIds);
        System.out.println("실패한 사용자 ID: " + failedUserIds);
        System.out.println("남은 쿠폰 수량: " + remainCouponsQuantity);

        // 최종 DB에서 capacity 재확인
        long finalCapacity = jpaCouponRepository.findById(savedCoupon.getCouponId())
                .map(CouponEntity::getCapacity)
                .orElseThrow()
                .longValue();

        System.out.println("DB 상 최종 쿠폰 capacity: " + finalCapacity);
        Assertions.assertEquals(conponCapacity, successUserIds.size());
    }
}
