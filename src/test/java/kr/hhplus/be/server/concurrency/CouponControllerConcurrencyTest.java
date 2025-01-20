package kr.hhplus.be.server.concurrency;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.user.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    void getCouponConcurrencyTest() throws Exception {
        // given
        UserEntity userEntity = new UserEntity("testUser");
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, 5L, LocalDate.now().plusDays(10)
        );

        UserEntity savedUser = jpaUserRepository.save(userEntity);
        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);

        List<Integer> successUserIds = new ArrayList<>();
        List<Integer> failedUserIds = new ArrayList<>();

        // 동시 요청을 처리하기 위해 Thread Pool 생성
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 10명의 사용자 요청을 실행
        Future<?>[] futures = new Future[10];
        for (int i = 0; i < 10; i++) {
            final int userId = i + 1;
            futures[i] = executorService.submit(() -> {
                try {
                    given()
                            .contentType(ContentType.JSON)
                            .pathParam("couponId", savedCoupon.getCouponId())
                            .body(userId)
                            .when()
                            .post("/api/coupons/{couponId}/get")
                            .then()
                            .log().all()
                            .statusCode(HttpStatus.OK.value())
                            .body("couponId", equalTo(savedCoupon.getCouponId().intValue()))
                            .body("couponName", equalTo("testCoupon1"));
                    synchronized (successUserIds) {
                        successUserIds.add(userId);
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

        // 결과 검증
        long successfulCount = jpaCouponRepository.findById(savedCoupon.getCouponId())
                .map(CouponEntity::getCapacity)
                .orElseThrow()
                .longValue();
        assert successUserIds.size() == 5 : "성공한 요청 수가 쿠폰 수량과 일치하지 않습니다.";
        assert failedUserIds.size() == 5 : "실패한 요청 수가 기대값과 일치하지 않습니다.";
    }
}
