package kr.hhplus.be.server.concurrency;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.goods.entity.GoodsEntity;
import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;
import kr.hhplus.be.server.domain.order.entity.OrderDetailEntity;
import kr.hhplus.be.server.domain.order.entity.OrderEntity;
import kr.hhplus.be.server.domain.payment.entity.PaymentEntity;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class GoodsStockConcurrencyTest extends IntergrationTest {
    @Test
    @DisplayName("[POST] /payments/{paymentId}/pessimistic  - 상품 재고 차감 요청 동시성 테스트 - 비관적 락")
    void confirmPaymentPessimistic() throws InterruptedException {
        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 50L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 30L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        List<Long> successUserIds = Collections.synchronizedList(new ArrayList<>());
        List<Long> failedUserIds = Collections.synchronizedList(new ArrayList<>());
        List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (i + 1);
            UserEntity userEntity = new UserEntity("test");
            userEntity.setPoint(500000L);
            UserEntity savedUser = jpaUserRepository.save(userEntity);

            OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
            OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

            OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
            OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
            jpaOrderDetailRepository.save(orderDetailEntity1);
            jpaOrderDetailRepository.save(orderDetailEntity2);

            PaymentEntity paymentEntity = new PaymentEntity(savedOrder.getOrderId(), 40000L);
            PaymentEntity savedPayment = jpaPaymentRepository.save(paymentEntity);

            executor.execute(() -> {
                try {
                    var response =
                            given()
                                    .contentType(ContentType.JSON)
                                    .pathParam("paymentId", savedPayment.getPaymentId())
                                    .body(userId)
                                    .when()
                                    .post("/api/payments/{paymentId}/pessimistic")
                                    .then()
                                    .log().all()
                                    .extract();
                    int statusCode = response.statusCode();
                    if (statusCode == 200) {
                        successUserIds.add(userId);
                    } else {
                        failedUserIds.add(userId);
                        String errorMessage = response.body().asString();
                        errorMessages.add(errorMessage);
                    }
                } catch (Exception e) {
                    failedUserIds.add(userId);
                    errorMessages.add(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("성공한 유저 IDs: " + successUserIds);
        System.out.println("실패한 유저 IDs: " + failedUserIds);
        System.out.println("에러 메시지: " + errorMessages);

        assertTrue(errorMessages.stream().anyMatch(msg -> msg.contains("재고가 부족합니다.")));
        assertEquals(3, successUserIds.size());
        assertEquals(10, successUserIds.size() + failedUserIds.size());
    }

    @Test
    @DisplayName("[POST] /payments/{paymentId}/optimistic  - 상품 재고 차감 요청 동시성 테스트 - 낙관적 락")
    void confirmPaymentOptimistic() throws InterruptedException {
        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 50L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 30L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        List<Long> successUserIds = Collections.synchronizedList(new ArrayList<>());
        List<Long> failedUserIds = Collections.synchronizedList(new ArrayList<>());
        List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (i + 1);
            UserEntity userEntity = new UserEntity("test");
            userEntity.setPoint(500000L);
            UserEntity savedUser = jpaUserRepository.save(userEntity);

            OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
            OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

            OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
            OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
            jpaOrderDetailRepository.save(orderDetailEntity1);
            jpaOrderDetailRepository.save(orderDetailEntity2);

            PaymentEntity paymentEntity = new PaymentEntity(savedOrder.getOrderId(), 40000L);
            PaymentEntity savedPayment = jpaPaymentRepository.save(paymentEntity);

            executor.execute(() -> {
                try {
                    var response =
                            given()
                                    .contentType(ContentType.JSON)
                                    .pathParam("paymentId", savedPayment.getPaymentId())
                                    .body(userId)
                                    .when()
                                    .post("/api/payments/{paymentId}/optimistic")
                                    .then()
                                    .log().all()
                                    .extract();
                    int statusCode = response.statusCode();
                    if (statusCode == 200) {
                        successUserIds.add(userId);
                    } else {
                        failedUserIds.add(userId);
                        String errorMessage = response.body().asString();
                        errorMessages.add(errorMessage);
                    }
                } catch (Exception e) {
                    failedUserIds.add(userId);
                    errorMessages.add(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("성공한 유저 IDs: " + successUserIds);
        System.out.println("실패한 유저 IDs: " + failedUserIds);
        System.out.println("에러 메시지: " + errorMessages);

        assertTrue(errorMessages.stream().anyMatch(msg -> msg.contains("동시성 문제가 발생했습니다. 잠시 후 다시 시도해주세요.")));
        assertEquals(3, successUserIds.size());
        assertEquals(10, successUserIds.size() + failedUserIds.size());
    }
}
