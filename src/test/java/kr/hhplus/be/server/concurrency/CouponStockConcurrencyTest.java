package kr.hhplus.be.server.concurrency;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.response.CouponResponse;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class CouponStockConcurrencyTest extends IntergrationTest {

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
    @DisplayName("[POST] /api/coupons/{couponId}/get - 쿠폰 발급 요청 동시성 테스트")
    void issueCouponDistributedConcurrencyTest() {
        Long couponCapacity = 7L;
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, couponCapacity, LocalDate.now().plusDays(10)
        );

        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);
        List<UserRequestLog> requestLogs = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Future<?>[] futures = new Future[10];
        for (int i = 0; i < 10L; i++) {
            final Long userId = (long) (i + 1);
            futures[i] = executorService.submit(() -> {
                LocalDateTime requestTime = LocalDateTime.now();
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
                        requestLogs.add(new UserRequestLog(userId, requestTime, "Success"));
                    } else if (statusCode == 400) {
                        String errorMessage = response.body().asString();
                        requestLogs.add(new UserRequestLog(userId, requestTime, "Failed - " + errorMessage));
                    } else {
                        String errorMessage = response.body().asString();
                        requestLogs.add(new UserRequestLog(userId, requestTime, "Failed - " + errorMessage));
                    }
                } catch (Exception e) {
                    requestLogs.add(new UserRequestLog(userId, requestTime, "Failed - " + e.getMessage()));
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

        requestLogs.sort((log1, log2) -> log1.requestTime.compareTo(log2.requestTime));
        requestLogs.forEach(System.out::println);
    }
}
