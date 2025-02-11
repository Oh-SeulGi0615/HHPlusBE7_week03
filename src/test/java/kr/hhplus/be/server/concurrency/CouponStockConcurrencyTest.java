package kr.hhplus.be.server.concurrency;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.response.CouponResponse;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
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

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get/pessimistic - 쿠폰 발급 요청 동시성 테스트 - 비관적 락")
    void issueCouponPessimisticConcurrencyTest() {
        Long couponCapacity = 8L;
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, couponCapacity, LocalDate.now().plusDays(10)
        );

        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);

        List<Long> successUserIds = Collections.synchronizedList(new ArrayList<>());
        List<Long> failedUserIds = Collections.synchronizedList(new ArrayList<>());
        List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());

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
                                    .post("/api/coupons/{couponId}/get/pessimistic")
                                    .then()
                                    .log().all()
                                    .extract();
                    int statusCode = response.statusCode();
                    if (statusCode == 200) {
                        CouponResponse couponResp = response.as(CouponResponse.class);
                        successUserIds.add(userId);
                    } else if (statusCode == 400) {
                        failedUserIds.add(userId);
                        String errorMessage = response.body().asString();
                        errorMessages.add(errorMessage);
                    } else {
                        failedUserIds.add(userId);
                    }
                } catch (Exception e) {
                    failedUserIds.add(userId);
                    errorMessages.add(e.getMessage());
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

        System.out.println("성공한 유저 IDs: " + successUserIds);
        System.out.println("실패한 유저 IDs: " + failedUserIds);
        System.out.println("에러 메시지: " + errorMessages);

        assertTrue(errorMessages.stream().anyMatch(msg -> msg.contains("쿠폰이 모두 소진되었습니다.")));
        assertEquals(couponCapacity, successUserIds.size());
        assertEquals(10 - couponCapacity, failedUserIds.size());
    }

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get/optimistic - 쿠폰 발급 요청 동시성 테스트 - 낙관적 락")
    void issueCouponOptimisticConcurrencyTest() throws InterruptedException{
        Long couponCapacity = 3L;
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, couponCapacity, LocalDate.now().plusDays(10)
        );

        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);

        List<Long> failList = Collections.synchronizedList(new ArrayList<>());
        List<Long> successList = Collections.synchronizedList(new ArrayList<>());

        int threadCount = 5;
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (i + 1);
            executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }

                    var response =
                            given()
                                    .contentType(ContentType.JSON)
                                    .pathParam("couponId", savedCoupon.getCouponId())
                                    .body(userId)
                                    .when()
                                    .post(" /api/coupons/{couponId}/get/optimistic")
                                    .then()
                                    .log().all()
                                    .extract()
                                    .response();

                    if (response.statusCode() == 200) {
                        successList.add(userId);
                    } else {
                        failList.add(userId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();

        doneLatch.await();
        executorService.shutdown();

        System.out.println("성공한 유저 IDs: " + successList);
        System.out.println("실패한 유저 IDs: " + failList);
        assertTrue(successList.size() == couponCapacity);
    }

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get - 쿠폰 발급 요청 동시성 테스트 - 분산 락")
    void issueCouponDistributedConcurrencyTest() {
        Long couponCapacity = 8L;
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, couponCapacity, LocalDate.now().plusDays(10)
        );

        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);

        List<Long> successUserIds = Collections.synchronizedList(new ArrayList<>());
        List<Long> failedUserIds = Collections.synchronizedList(new ArrayList<>());
        List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());

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
                        successUserIds.add(userId);
                    } else if (statusCode == 400) {
                        failedUserIds.add(userId);
                        String errorMessage = response.body().asString();
                        errorMessages.add(errorMessage);
                    } else {
                        String errorMessage = response.body().asString();
                        errorMessages.add(errorMessage);
                        failedUserIds.add(userId);
                    }
                } catch (Exception e) {
                    failedUserIds.add(userId);
                    errorMessages.add(e.getMessage());
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

        System.out.println("성공한 유저 IDs: " + successUserIds);
        System.out.println("실패한 유저 IDs: " + failedUserIds);
        System.out.println("에러 메시지: " + errorMessages);

        assertTrue(errorMessages.stream().anyMatch(msg -> msg.contains("쿠폰이 모두 소진되었습니다.")));
        assertEquals(couponCapacity, successUserIds.size());
        assertEquals(10 - couponCapacity, failedUserIds.size());
    }
}
