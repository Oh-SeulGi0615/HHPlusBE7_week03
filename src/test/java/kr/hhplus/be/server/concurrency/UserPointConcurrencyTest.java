package kr.hhplus.be.server.concurrency;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.user.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class UserPointConcurrencyTest extends IntergrationTest {
    @Test
    @DisplayName("[POST] /api/users/{userId}/points/charge - 유저 포인트 충전 동시성 테스트 - 낙관적 락")
    void userPointChargeConcurrencyTest() throws InterruptedException {
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        int threadCount = 3;
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Long> optimisticLockExceptionList = Collections.synchronizedList(new ArrayList<>());
        List<Long> successList = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final Long idx = (long) i;
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
                                    .pathParam("userId", savedUser.getUserId())
                                    .body(50000L)
                                    .when()
                                    .post("/api/users/{userId}/points/charge")
                                    .then()
                                    .log().all()
                                    .extract()
                                    .response();

                    if (response.statusCode() == 200) {
                        successList.add(idx);
                    } else if (response.statusCode() == 500) {
                        optimisticLockExceptionList.add(idx);
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

        System.out.println("OptimisticLockException 발생 리스트: " + optimisticLockExceptionList);
        System.out.println("성공 요청 리스트: " + successList);

        assertEquals(1, successList.size());
        assertEquals(optimisticLockExceptionList.size(), threadCount - 1);
    }

}
